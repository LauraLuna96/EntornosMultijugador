package spacewar;

import java.util.concurrent.atomic.AtomicInteger;

public class Spaceship extends SpaceObject {

	private static final double SPACESHIP_SPEED = 0.6;
	private static final double SPACESHIP_BRAKES = 0.90;
	private static final double SPACESHIP_ROTATION_SPEED = 3.00;
	private static final int SPACESHIP_COLLISION_FACTOR = 400;
	private static final double SPACE_FRICTION = 0.95;
	
	private AtomicInteger propellerUses; //Propulsor

	class LastMovement {
		boolean thrust = false;
		boolean brake = false;
		boolean rotLeft = false;
		boolean rotRight = false;
		boolean propeller = false;
	}

	private LastMovement lastMovement;

	public Spaceship() {
		this.propellerUses = new AtomicInteger(3); // Tendrá 4 posibles valores (0,1,2,3) siendo 3 el máximo
		this.setCollisionFactor(SPACESHIP_COLLISION_FACTOR);
		// Randomize
		this.initSpaceship(Math.random() * 1000, Math.random() * 600, Math.random() * 360);
	}
	
	public int getPropellerUses() {
		return this.propellerUses.get();
	}
	
	public void decreasePropellerUses() {
		this.propellerUses.decrementAndGet();
	}
	
	public void increasePropellerUses(int n) {
		this.propellerUses.addAndGet(n);
	}

	public void initSpaceship(double coordX, double coordY, double facingAngle) {
		this.setPosition(coordX, coordY);
		this.setVelocity(0, 0);
		this.setFacingAngle(facingAngle);
		lastMovement = new LastMovement();
	}

	public void loadMovement(boolean thrust, boolean brake, boolean rotLeft, boolean rotRight, boolean propeller) {
		this.lastMovement.thrust = thrust;
		this.lastMovement.brake = brake;
		this.lastMovement.rotLeft = rotLeft;
		this.lastMovement.rotRight = rotRight;
		this.lastMovement.propeller = propeller;
	}

	public void calculateMovement() {
		if (this.lastMovement.propeller) {
			this.lastMovement.propeller = false;
			decreasePropellerUses();
			double coordX = Math.random() * 1000;
			double coordY = Math.random() * 600;
			this.setPosition(coordX, coordY);
		}
		this.multVelocity(SPACE_FRICTION);

		if (this.lastMovement.thrust) {
			this.incVelocity(Math.cos(this.getFacingAngle() * Math.PI / 180) * SPACESHIP_SPEED,
					Math.sin(this.getFacingAngle() * Math.PI / 180) * SPACESHIP_SPEED);
		}

		if (this.lastMovement.brake) {
			this.multVelocity(SPACESHIP_BRAKES);
		}

		if (this.lastMovement.rotLeft) {
			this.incFacingAngle(-SPACESHIP_ROTATION_SPEED);
		}

		if (this.lastMovement.rotRight) {
			this.incFacingAngle(SPACESHIP_ROTATION_SPEED);
		}

		this.applyVelocity2Position();

		lastMovement = new LastMovement();
	}
}
