<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="./css/latte.css">
<link rel="stylesheet" type="text/css" href="./css/table.css">
<link rel="stylesheet" type="text/css" href="./css/input.css">
<link rel="stylesheet" type="text/css" href="./css/message.css">
<title>ログイン画面</title>
</head>
<body>
	<!--ヘッダー-->
	<div id="header">
		<jsp:include page="header.jsp"/>
	</div>

	<!-- コンテンツ -->
	<div id="contents">
		<h1>ログイン画面</h1>

			<!-- エラーメッセージ -->
			<s:if test="userIdErrorMessageList!=null && userIdErrorMessageList.size()>0">
				<div class="error-message"><s:iterator value="userIdErrorMessageList"><s:property /><br></s:iterator></div>
			</s:if>
			<s:if test="passwordErrorMessageList!=null && passwordErrorMessageList.size()>0">
				<div class="error-message"><s:iterator value="passwordErrorMessageList"><s:property /><br></s:iterator></div>
			</s:if>
			<s:if test="isNotUserInfoMessage!=null && !isNotUserInfoMessage.isEmpty()">
				<div class="error-message"><s:property value="isNotUserInfoMessage"/></div>
			</s:if>

			<!-- 入力テーブル -->
			<s:form action="LoginAction">
			<table class="c-table">
				<tr>
					<th scope="row"><s:label value="ユーザーID"/></th>
					<s:if test="#session.savedUserIdFlg==true">
						<td><s:textfield name="userId" class="txt" placeholder="ユーザーID" value='%{#session.userId}' autocomplete="off"/></td>
					</s:if>
					<s:else>
						<td><s:textfield name="userId" class="txt" placeholder="ユーザーID" value ='%{userId}' autocomplete="off"/></td>
					</s:else>
				</tr>
				<tr>
					<th scope="row"><s:label value="パスワード"/></th>
					<td><s:password name="password" class="txt" placeholder="パスワード"  autocomplete="off"/></td>
				</tr>
			</table>

			<!-- チェックボックス -->
			<div class="checkbox">
				<s:if test="(#session.savedUserIdFlg==true && #session.userId!=null && !#session.userId.isEmpty()) || savedUseridFlg==true">
					<s:checkbox name="savedUserIdFlg" checked="checked"/>
				</s:if>
				<s:else>
					<s:checkbox name="savedUserIdFlg"/>
				</s:else>
				<s:label value="ユーザーID保存"/><br>
			</div>

			<!-- フォームボタン -->
			<div class="submit_btn_box">
				<div>
					<s:submit value="ログイン" class="submit_btn"/>
				</div>
			</div>
				</s:form>

			<div class="submit_btn_box">
				<s:form action="CreateUserAction">
					<div>
						<s:submit value="新規ユーザー登録" class="submit_btn"/>
					</div>
				</s:form>
			</div>
			<div class="submit_btn_box">
				<s:form action="ResetPasswordAction">
					<div>
						<s:submit value="パスワード再設定" class="submit_btn"/>
					</div>
				</s:form>
			</div>

	</div>

</body>
</html>
