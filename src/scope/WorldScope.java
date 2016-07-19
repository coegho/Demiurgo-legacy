package scope;

import plataformarol.StoredSymbol;
import universe.UserDefinedClass;
import universe.World;
import universe.WorldObject;
import universe.WorldRoom;
import values.IReturnValue;

public class WorldScope extends Scope {
	//TODO: Has world its own variables?
	protected World world;
	
	public WorldScope(World world) {
		this.world = world;
	}
	
	@Override
	public StoredSymbol getVariable(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVariable(String name, IReturnValue value) {
		// TODO Auto-generated method stub
		
	}
	
	public World getWorld() {
		return world;
	}

	public UserDefinedClass getClassFromName(String className) {
		return world.getClassFromName(className);
	}

	public UserDefinedClass getRootClass() {
		return world.getRootClass();
	}

	public void addClass(String className, UserDefinedClass newClass) {
		world.addClass(className, newClass);
		
	}

	public WorldObject getObject(long id) {
		return world.getObject(id);
	}

	public WorldRoom getRoom(String roomLongName) {
		return world.getRoom(roomLongName);
	}

	public WorldRoom getRoom(String roomRelativeName, String currentRoom) {
		return world.getRoom(roomRelativeName, currentRoom);
	}

	public void setUserObject(String user, WorldObject obj) {
		world.setUserObject(user, obj);
	}
}