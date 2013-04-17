package benworks.spring.aop;


public interface UserManagerInf {
	
	void create(String name, String password);
	
	void delete(String name);
	
	void changePassword(String name, String oldPassword, String newPassword);
}
