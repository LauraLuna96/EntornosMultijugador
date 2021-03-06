package spacewar;

public class LifePowerUp extends GenericPowerUp {

	private final int life = 1;

	public LifePowerUp(int id) {
		super(id, "LIFE");
	}

	@Override
	public void applyPowerUp(Player player) {
		player.increaseLife(life);
		player.increaseScore(5);
	}

}
