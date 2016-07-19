package scope;

import java.util.HashMap;
import java.util.Map;

import plataformarol.StoredSymbol;
import values.IReturnValue;

/**
 * 
 * @author Martín Coego Pérez
 * @version %I%, %G%
 * @since 1.0
 *
 */
public class LoopScope extends Scope {

	protected Scope parent;
	protected Map<String, StoredSymbol> variables;
	
	public LoopScope(Scope parent) {
		this.parent = parent;
		variables = new HashMap<>();
	}
	
	@Override
	public StoredSymbol getVariable(String name) {
		if(variables.containsKey(name)) {
			return variables.get(name);
		}
		else {
			return parent.getVariable(name);
		}
	}

	@Override
	public void setVariable(String name, IReturnValue value) {
		variables.put(name, new StoredSymbol(value));
	}

}