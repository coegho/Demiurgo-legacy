package universe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import serializable.SerializableDecision;

/**
 * Represents a world into the system.
 * 
 * @author Martín Coego Pérez
 * @version %I%, %G%
 * @since 1.0
 *
 */
public class World {
	protected String name;
	protected Map<String, UserDefinedClass> classes;
	protected Map<Long, WorldObject> objects;
	protected Map<Long, WorldLocation> locations;
	protected Map<String, User> users;
	protected Map<User, WorldObject> userObjects;
	protected RoomGroup rooms;
	protected UserDefinedClass rootClass;
	protected long currentObjId;
	protected long currentRoomId;
	protected Map<User, String> untraceableDecisions;
	protected List<WorldRoom> pendingRooms;

	public World(String name) {
		this.name = name;
		currentObjId = 0;
		currentRoomId = 0;
		classes = new HashMap<>();
		objects = new HashMap<>();
		locations = new HashMap<>();
		users = new HashMap<>();
		userObjects = new HashMap<>();
		rooms = new RoomGroup("", null);
		rootClass = new RootObjectClass(this);
		untraceableDecisions = new HashMap<>();
		pendingRooms = new ArrayList<>();
		classes.put("object", rootClass);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserDefinedClass getClassFromName(String className) {
		return classes.get(className);
	}

	public void addClass(UserDefinedClass newClass) {
		classes.put(newClass.getClassName(), newClass);
	}

	public void removeClass(String className) {
		classes.remove(className);
	}

	public UserDefinedClass getRootClass() {
		return rootClass;
	}

	public void setRootClass(UserDefinedClass rootClass) {
		this.rootClass = rootClass;
	}

	public void addObject(WorldObject obj) {
		if (obj.getId() == -1) {
			obj.setId(currentObjId);
			currentObjId++;
		}
		objects.put(obj.getId(), obj);
	}

	public void removeObject(WorldObject obj) {
		objects.remove(obj.id);
	}

	public WorldObject getObject(long id) {
		return objects.get(id);
	}

	/**
	 * Moves an object from one location to another.
	 * 
	 * @param origin
	 *            Original location.
	 * @param destination
	 *            Future location.
	 * @param obj
	 *            Moved object.
	 */
	public void moveTo(WorldLocation origin, WorldLocation destination, WorldObject obj) {
		origin.removeObject(obj);
		obj.setLocation(destination);
		destination.addObject(obj);
	}

	/**
	 * Finds a RoomGroup by its absolute path.
	 * 
	 * @param loc
	 *            Absolute path to the room.
	 * @param constructive
	 *            If true, creates RoomGroup objects when they are not found.
	 * @return
	 */
	protected RoomGroup searchRoomGroup(String loc, boolean constructive) {
		RoomGroup rg = rooms;

		for (String s : loc.split("/")) {
			if (rg.getChildren().containsKey(s)) {
				rg = rg.getChildren().get(s);
			} else {
				if (constructive) {
					RoomGroup ng = new RoomGroup(s, rg);
					rg.getChildren().put(s, ng);
					rg = ng;
				} else
					return null;
			}
		}

		return rg;
	}

	/**
	 * Creates a new room from a relative path.
	 * 
	 * @param roomRelativeName
	 *            Relative path to the room.
	 * @param currentRoom
	 *            The absolute path of the current room (ending by '/').
	 * @return
	 */
	public WorldRoom newRoom(String roomRelativeName, String currentRoom) {
		return newRoom(currentRoom + roomRelativeName);
	}

	/**
	 * Insert a new room on the specified path.
	 * 
	 * @param room
	 *            The relative path to the room.
	 * @return
	 */
	public WorldRoom newRoom(WorldRoom room) {
		RoomGroup rg = searchRoomGroup(room.getLongPath(), true);
		rg.setOwnRoom(room);
		locations.put(room.getId(), room);
		return rg.getOwnRoom();

	}

	/**
	 * Creates a new room from a absolute path.
	 * 
	 * @param roomLongName
	 *            The complete path to the room.
	 * @return
	 */
	public WorldRoom newRoom(String roomLongName) {
		RoomGroup rg = searchRoomGroup(roomLongName, true);
		if (rg.getOwnRoom() == null) {
			WorldRoom room = new WorldRoom(roomLongName, this, currentRoomId);
			currentRoomId++;
			rg.setOwnRoom(room);
			locations.put(room.getId(), room);
		} else {
			System.err.println("Room already exists"); // TODO
		}
		return rg.getOwnRoom();
	}

	/**
	 * Returns a room from a relative path.
	 * 
	 * @param roomRelativeName
	 *            The relative path to the room.
	 * @param currentRoom
	 *            The absolute path of the current room (ending by '/').
	 * @return
	 */
	public WorldRoom getRoom(String roomRelativeName, String currentRoom) {
		return getRoom(currentRoom + roomRelativeName);
	}

	/**
	 * Returns a room from a absolute path.
	 * 
	 * @param roomLongName
	 *            The complete path to the room.
	 * @return
	 */
	public WorldRoom getRoom(String roomLongName) {
		RoomGroup rg = searchRoomGroup(roomLongName, false);
		if (rg == null)
			return null;
		else
			return rg.getOwnRoom();
	}

	/**
	 * Debug method. It returns all rooms in the world. TODO
	 * 
	 * @return
	 */
	public List<WorldRoom> getAllRooms() {
		return rooms.getAllRooms();
	}

	/**
	 * Debug method. It returns all classes in the world. TODO
	 * 
	 * @return
	 */
	public Set<String> getAllClasses() {
		return classes.keySet();
	}

	public Set<String> getAllUserNames() {
		return users.keySet();
	}

	public Set<Long> getAllObjIds() {
		return objects.keySet();
	}

	/**
	 * Get the object associated to the given user.
	 * 
	 * @param user
	 *            Username
	 * @return The user's object
	 */
	public WorldObject getObjectFromUser(String user) {
		return userObjects.get(user);
	}

	/**
	 * Links an object with an user.
	 * 
	 * @param user
	 * @param obj
	 */
	public void setUserObject(User user, WorldObject obj) {
		if (obj.getUser() != null) {
			userObjects.remove(obj.getUser());
		}
		userObjects.put(user, obj);
		obj.setUser(user);
	}

	/**
	 * Adds a new user.
	 * 
	 * @param user
	 */
	public void addUser(User user) {
		users.put(user.getUsername(), user);
	}

	/**
	 * Get an existing user.
	 * 
	 * @param user
	 * @return
	 */
	public User getUser(String user) {
		return users.get(user);
	}

	public WorldLocation getLocation(long loc_id) {
		return locations.get(loc_id);
	}

	public void setCurrentObjId(long objId) {
		this.currentObjId = objId;
	}

	public void setCurrentRoomId(long roomId) {
		this.currentRoomId = roomId;
	}

	public long getCurrentObjId() {
		return currentObjId;
	}

	public long getCurrentRoomId() {
		return currentRoomId;
	}

	public void addDecision(User user, String text) {
		if(user.getObj() != null && user.getObj().getLocation() instanceof WorldRoom) {
			WorldRoom room = (WorldRoom)user.getObj().getLocation();
			pendingRooms.add(room);
			room.addDecision(user, text);
		}
		else {
			untraceableDecisions.put(user, text);
		}
	}

	public List<SerializableDecision> getSerializableDecisions() {
		List<SerializableDecision> l = new ArrayList<>();
		for(User u : untraceableDecisions.keySet()) {
			l.add(new SerializableDecision(u.getUsername(), null, untraceableDecisions.get(u)));
		}
		return l;
	}

	public List<WorldRoom> getPendingRooms() {
		return pendingRooms;
	}
	
}
