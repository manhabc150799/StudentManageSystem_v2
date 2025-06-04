public class User {

	private String userId;

	private String email;

	private String password;
	
	private String fullName;

	private String role;

	private boolean status;

	private String dob;
	
	

	public User(String userId, String email, String password, String fullName, String role, boolean status, String dob) {
		super();
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.role = role;
		this.status = status;
		this.dob = dob;
	}

	public boolean authenticate(String inputPassword) {
		return this.password.equals(inputPassword);
	}

	public void changePassword(String oldPassword, String newPassword) {
		if (authenticate(oldPassword)) {
            this.password = newPassword;
            System.out.println("Password has been updated successfully.");
        } else {
            System.out.println("Old password is incorrect. Password update failed.");
        }
	}

	public boolean hasRole(String requiredRole) {
		return this.role.equalsIgnoreCase(requiredRole);
	}

	//Getters and Setters
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	
}
