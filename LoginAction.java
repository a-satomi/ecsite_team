package com.internousdev.mars.action;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import com.internousdev.mars.dao.CartInfoDAO;
import com.internousdev.mars.dao.UserInfoDAO;
import com.internousdev.mars.dto.CartInfoDTO;
import com.internousdev.mars.dto.UserInfoDTO;
import com.internousdev.mars.util.InputChecker;

public class LoginAction extends ActionSupport implements SessionAware {

	private String userId;
	private String password;
	private boolean savedUserId;

	private Map<String, Object> session;

	private List<String> userIdErrorMessageList = new ArrayList<String>();
	private List<String> passwordErrorMessageList = new ArrayList<String>();
	private List<String> userIdNotMatchPasswordErrorMessageList = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public String execute() {

		// セッションタイムアウト確認
		if(session.isEmpty()){
			return "sessionError";
		}

		if(userId == null || password == null) {
			return ERROR;
		}

		String result = ERROR;

		// チェックボックスにチェックを表示/非表示にする
		if(savedUserId == true) {
			session.put("savedUserId", true);
			session.put("userId", userId);
		} else {
			session.put("savedUserId", false);
			session.remove("userId", userId);
		}

		// エラーメッセージリストに表示するエラー文をセット
		InputChecker inputChecker = new InputChecker();
		UserInfoDAO userInfoDao = new UserInfoDAO();

		userIdErrorMessageList = inputChecker.doCheck("ユーザーID", userId, 1, 8, true, false, false, true, false, false, false);
		passwordErrorMessageList = inputChecker.doCheck("パスワード", password, 1, 16, true, false, false, true, false, false, false);

		// エラーメッセージリストの要素数チェック
		if(userIdErrorMessageList.size() == 0
				&& passwordErrorMessageList.size() == 0) {

			// ユーザーIDとパスワードの照合
			if(userInfoDao.isExistsUserInfo(userId, password)) {
				if(userInfoDao.login(userId, password) > 0) {

					// カート情報をユーザーに紐づける
					List<CartInfoDTO> cartInfoDtoList = new ArrayList<CartInfoDTO>();

					// sessionからカート情報を取得する
					cartInfoDtoList = (List<CartInfoDTO>)session.get("cartInfoDtoList");
					if(cartInfoDtoList != null) {
						if(!chengeCartInfo(cartInfoDtoList)) {
							return ERROR;
						}
					}

					// 遷移先を設定する（カート画面）
					if(session.containsKey("cartFlg")) {
						session.remove("cartFlg");
						result = "cart";
					} else {
						result = SUCCESS;
					}

					// ユーザー情報をsessionに登録する
					UserInfoDTO userInfoDto = userInfoDao.userInfo(userId, password);
					session.put("userId", userInfoDto.getUserId());
					session.put("loginFlg", 1);
				}
			} else {
				userIdNotMatchPasswordErrorMessageList.add("ユーザーIDまたはパスワードが異なります。");
			}
		} else {
			session.put("loginFlg", 0);
		}
		return result;
	}

	/**
	 * カート情報をDBに作成/更新をする
	 * @param cartInfoDtoList 画面で選択したカート情報
	 * @return 作成/更新成功した場合：true、失敗した場合：false
	 */
	public boolean chengeCartInfo(List<CartInfoDTO> cartInfoDtoList) {

		int count = 0;
		boolean result = false;
		CartInfoDAO cartInfoDao = new CartInfoDAO();
		String tempUserId = session.get("tempUserId").toString();

		for(CartInfoDTO dto : cartInfoDtoList) {

			// ユーザーIDと商品IDが一致するカート情報が存在するかチェック
			if(cartInfoDao.isExistsCartInfo(userId, dto.getProductId())) {

				// 存在する場合、カート情報テーブルの購入個数を更新/tempUserIdのデータを削除
				count += cartInfoDao.updateProductCount(userId, dto.getProductId(), dto.getProductCount());
				cartInfoDao.delete(String.valueOf(dto.getProductId()), tempUserId);
			} else {

				// 存在しない場合、ユーザーIDをtempUserId→userIdに変更
				count += cartInfoDao.linkToUserId(tempUserId, userId, dto.getProductId());
			}
		}

		// DBへの処理件数とカート情報数が一致しているかをチェック
		if(count == cartInfoDtoList.size()) {
			// DBからuserIdに紐づくカート情報を取得
			List<CartInfoDTO> newCartInfoDtoList = cartInfoDao.getCartInfoDtoList(userId);
			Iterator<CartInfoDTO> iterator = newCartInfoDtoList.iterator();

			if(!(iterator.hasNext())) {
				newCartInfoDtoList = null;
			}
			session.put("cartInfoDtoList", newCartInfoDtoList);
			result = true;
		}
		return result;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getSavedUserId() {
		return savedUserId;
	}

	public void setSavedUserId(boolean savedUserId) {
		this.savedUserId = savedUserId;
	}

	public List<String> getUserIdErrorMessageList() {
		return userIdErrorMessageList;
	}

	public void setUserIdErrorMessageList(List<String> userIdErrorMessageList) {
		this.userIdErrorMessageList = userIdErrorMessageList;
	}

	public List<String> getPasswordErrorMessageList() {
		return passwordErrorMessageList;
	}

	public void setPasswordErrorMessageList(List<String> passwordErrorMessageList) {
		this.passwordErrorMessageList = passwordErrorMessageList;
	}

	public List<String> getUserIdNotMatchPasswordErrorMessageList() {
		return userIdNotMatchPasswordErrorMessageList;
	}

	public void setUserIdNotMatchPasswordErrorMessageList(List<String> userIdNotMatchPasswordErrorMessageList) {
		this.userIdNotMatchPasswordErrorMessageList = userIdNotMatchPasswordErrorMessageList;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}