<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<link rel="stylesheet" href="./css/login.css">
	<link rel="stylesheet" href="./css/mars.css">

	<title>ログイン</title>

	<script>
		function goLoginAction() {
			document.getElementById("form").action="LoginAction";
		}
		function goCreateUserAction() {
			document.getElementById("form").action="CreateUserAction";
		}
		function goResetPasswordAction() {
			document.getElementById("form").action="ResetPasswordAction";
		}
	</script>
</head>
<body>
<jsp:include page="header.jsp"/>

	<div id="main">
		<h1>ログイン画面</h1>
		<s:form id="form" action="LoginAction">
				<s:if test="!userIdNotMatchPasswordErrorMessageList.isEmpty()">
					<div class="error">
						<s:iterator value="userIdNotMatchPasswordErrorMessageList"><s:property /></s:iterator>
					</div>
				</s:if>
				<s:if test="!userIdErrorMessageList.isEmpty()">
					<div class="error">
						<s:iterator value="userIdErrorMessageList"><s:property /><br/></s:iterator>
					</div>
				</s:if>
				<s:if test="!passwordErrorMessageList.isEmpty()">
					<div class="error">
						<s:iterator value="passwordErrorMessageList"><s:property /><br/></s:iterator>
					</div>
				</s:if>

			<table class="t1">
				<tr>
					<th scope="row"><s:label value="ユーザーID"/></th>
					<s:if test="#session.savedUserId == true">
						<td><s:textfield name="userId" class="textfield" placeholder="ユーザーID" value='%{#session.userId}' autocomplete="off"/></td>
					</s:if>
					<s:else>
						<td><s:textfield name="userId" class="textfield" placeholder="ユーザーID" autocomplete="off"/></td>
					</s:else>
				</tr>
				<tr>
					<th scope="row"><s:label value="パスワード"/></th>
					<td><s:password name="password" class="textfield" placeholder="パスワード" autocomplete="off" /></td>
				</tr>
			</table>

			<div class="check_box">
				<s:if test="#session.savedUserId == true">
					<s:checkbox name="savedUserId" checked="checked"/>
				</s:if>
				<s:else>
					<s:checkbox name="savedUserId"/>
				</s:else>
				<s:label value="ユーザーID保存"/><br/>
			</div>

			<div class="login_btn_box">
				<s:submit value="ログイン" class="login_submit_btn" onclick="goLoginAction();"/>
			</div>
		</s:form>

		<div class="login_btn_box">
			<s:form action="CreateUserAction">
				<s:submit value="新規ユーザー登録" class="login_submit_btn"/>
			</s:form>
		</div>
		<div class="login_btn_box">
			<s:form action="ResetPasswordAction">
				<s:submit value="パスワード再設定" class="login_submit_btn"/>
			</s:form>
		</div>
	</div>

</body>
</html>