package universe;

public class Inventory extends WorldLocation {
	protected WorldObject container;

	public Inventory(World world) {
		super(world);
	}
	
	public WorldObject getContainer() {
		return container;
	}

	public void setContainer(WorldObject container) {
		this.container = container;
	}
	
	
}
