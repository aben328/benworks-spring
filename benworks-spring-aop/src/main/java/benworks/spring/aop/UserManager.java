package benworks.spring.aop;


public class UserManager implements UserManagerInf {
	
	@Override
	public void changePassword(String name, String oldPassword, String newPassword) {
		System.out.println("[UserManger] : changePassword " + this);
	}
	
	@Override
	@LogIt("创建用户")
	public void create(String name, String password) {
		System.out.println("[UserManger] : create " + this);
	}
	
	@Override
	public void delete(String name) {
		System.out.println("[UserManger] : delete " + this);
	}
	
}
