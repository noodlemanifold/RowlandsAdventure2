package rowlandsAdventure2;

public abstract class Enums {

    public enum CollisionGroups {
		Default(1),
		Static(2),
		Kinematic(4),
		Debris(8),
		Sensor(16),
		Character(32),
		Environment(64),
		Prop(128),
		All(-1),
		None(0);

		private final int value;

		CollisionGroups(final int newValue) {
			value = newValue;
		}

		public int value() {
			return value;
		}
	}
    
}
