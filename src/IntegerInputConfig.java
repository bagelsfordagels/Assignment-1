public class IntegerInputConfig implements InputConfig{
	int userInt;
	
	public IntegerInputConfig(int userInt) {
		this.userInt = userInt;
	}

	@Override
	public int getUserData() {
		// TODO Auto-generated method stub
		return userInt;
	}

	@Override
	public String getUserFileData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}