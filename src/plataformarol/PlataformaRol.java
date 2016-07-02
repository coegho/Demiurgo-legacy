package plataformarol;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import linguaxe.LinguaxeLexer;
import linguaxe.LinguaxeParser;
import universe.World;
import universe.WorldObject;
import universe.WorldRoom;

/**
 * 
 * @author Martín Coego Pérez
 * @version %I%, %G%
 * @since 1.0
 *
 */
public class PlataformaRol {

	public static void main(String[] args) {
		try {
			//TODO: example data
			World currentWorld = new World();
			WorldRoom currentRoom = currentWorld.newRoom("/proba/estancia_de_probas");
			currentWorld.newRoom("/proba/estancia2");

			if (args.length > 0) {
				for (String arg : args) {
					InputStream is = new FileInputStream(arg);
					ANTLRInputStream input = new ANTLRInputStream(is);
					LinguaxeLexer lexer = new LinguaxeLexer(input);
					CommonTokenStream tokens = new CommonTokenStream(lexer);
					LinguaxeParser parser = new LinguaxeParser(tokens);
					ParseTree tree = parser.s(); // parse; start at s
					ExecVisitor eval = new ExecVisitor(currentWorld, currentRoom);
					eval.visit(tree);
				}
			} else {
				InputStream is = System.in;
				ANTLRInputStream input = new ANTLRInputStream(is);
				LinguaxeLexer lexer = new LinguaxeLexer(input);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				LinguaxeParser parser = new LinguaxeParser(tokens);
				ParseTree tree = parser.s(); // parse; start at s
				ExecVisitor eval = new ExecVisitor(currentWorld, currentRoom);
				eval.visit(tree);
			}

			//Checking world state
			System.out.println("##########CHECKING WORLD STATE########");
			for(WorldRoom r : currentWorld.getAllRooms()) {
				System.out.println("ROOM " + r.getLongName());
				System.out.print("| OBJECTS = {");
				for(WorldObject o : r.getObjects()) {
					System.out.print(" #"+ o.getId() + ":"+o.getUserClass().getClassName());
				}
				System.out.println(" }");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
