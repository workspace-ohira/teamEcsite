package com.internousdev.latte.action;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.latte.dao.CartInfoDAO;
import com.internousdev.latte.dao.UserInfoDAO;
import com.internousdev.latte.dto.CartInfoDTO;
import com.internousdev.latte.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport implements SessionAware {

	private String userId;
	private String password;
	private List<String> userIdErrorMessageList;
	private List<String> passwordErrorMessageList;
	private String isNotUserInfoMessage;
	private List<CartInfoDTO> cartInfoDTO;
	private boolean savedUserIdFlg;
	public Map<String,Object> session;
	private int totalPrice;

	public String execute() throws SQLException {

		String result = ERROR;
		session.remove("savedUserIdFlg");

		//新規登録からの遷移
		if (session.containsKey("createUserFlg") && Integer.parseInt(session.get("createUserFlg").toString()) == 1) {
			userId = session.get("userIdForCreateUser").toString();
			//使わなくなったセッション情報は削除
			session.remove("userIdForCreateUser");
			session.remove("createUserFlg");
		} else {//それ以外は入力チェック
			InputChecker inputChecker = new InputChecker();
			userIdErrorMessageList = inputChecker.doCheck("ユーザーID",userId,1,8,true,false,false,true,false,false);
			passwordErrorMessageList = inputChecker.doCheck("パスワード", password, 1, 16, true, false, false, true, false, false);

			//listにエラーメッセージが入ってたら
			if (userIdErrorMessageList.size() > 0 || passwordErrorMessageList.size() > 0) {
				session.put("logined", 0);
				return result;
			}

			//ログイン認証
			UserInfoDAO userInfoDAO = new UserInfoDAO();
			//入力されたIDとパスワードとDBの値が一致するかチェック
			if (!userInfoDAO.isExistsUser(userId,password)) {
				isNotUserInfoMessage = "ユーザーIDまたはパスワードが異なります。";
				return result;
			}
		}

		//セッションタイムアウト
		if(!session.containsKey("tempUserId")) {
			return "sessionTimeout";
		}

		//カート情報の紐づけ
		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		String tempUserId = session.get("tempUserId").toString();
		List<CartInfoDTO> cartInfoDTOListForTempUser = cartInfoDAO.getCartInfoDTO(tempUserId);
		if (cartInfoDTOListForTempUser != null && cartInfoDTOListForTempUser.size() > 0) {
			boolean cartresult = changeCartInfo(cartInfoDTOListForTempUser,tempUserId);
			if (!cartresult) {
				return "DBerror";
			}
		}

		//認証情報保存
		//セッションで値を保持
		session.put("userId", userId);
		session.put("logined",1);
		if (savedUserIdFlg) {
			session.put("savedUserIdFlg",true);
		}
		//仮ユーザーIDは使わなくなったので削除
		session.remove("tempUserId");

		//遷移先決定
		if (session.containsKey("cartFlg") && Integer.parseInt(session.get("cartFlg").toString()) == 1) {
			//カートフラグ使用済み
			session.remove("cartFlg");
			cartInfoDTO = cartInfoDAO.getCartInfoDTO(userId);
			totalPrice = cartInfoDAO.getTotalPrice(userId);
			return "cart";
		} else {
			result = SUCCESS;
		}

		return result;
	}

	//DBのカート情報を更新する
	private boolean changeCartInfo(List<CartInfoDTO> cartInfoDTOListForTempUser,String tempUserId) {
		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		int count = 0;
		boolean result = false;

		for (CartInfoDTO dto : cartInfoDTOListForTempUser) {
			//仮ユーザーIDと本ユーザーIDのカート情報を比較し同じ商品IDのカート情報が存在するかチェック
			if (cartInfoDAO.isExistsProduct(userId,dto.getProductId())) {
				//存在する場合、仮ユーザーのカートの個数を足し仮ユーザー情報は削除。
				count += cartInfoDAO.updateCartInfo(userId,dto.getProductId(),dto.getProductCount());
				cartInfoDAO.deleteCartInfo(dto.getProductId(),tempUserId);
			} else {
				//存在しない場合仮ユーザーIDを本ユーザーIDに更新する
				count += cartInfoDAO.linkToUserId(userId,tempUserId,dto.getProductId());
			}
		}

		if (count == cartInfoDTOListForTempUser.size()) {
			result = true;
		}
		return result;
	}

	//ゲッターセッター
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
	public String getIsNotUserInfoMessage() {
		return isNotUserInfoMessage;
	}
	public void setIsNotUserInfoMessage(String isNotUserInfoMessage) {
		this.isNotUserInfoMessage = isNotUserInfoMessage;
	}
	public List<CartInfoDTO> getCartInfoDTO() {
		return cartInfoDTO;
	}
	public void setCartInfoDTO(List<CartInfoDTO> cartInfoDTO) {
		this.cartInfoDTO = cartInfoDTO;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	public boolean getSavedUserIdFlg() {
		return savedUserIdFlg;
	}
	public void setSavedUserIdFlg(boolean savedUserIdFlg) {
		this.savedUserIdFlg = savedUserIdFlg;
	}
	public Map<String,Object> getSession() {
		return session;
	}
	@Override
	public void setSession(Map<String,Object> session) {
		this.session = session;
	}
}