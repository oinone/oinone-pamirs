package pro.shushi.pamirs.meta.dsl.definition.helper;

import com.thoughtworks.xstream.XStream;
import pro.shushi.pamirs.meta.dsl.definition.node.Exception;
import pro.shushi.pamirs.meta.dsl.definition.node.*;
import pro.shushi.pamirs.meta.dsl.definition.node.process.*;

public class XStreamHelper {

	public volatile static XStream xstream;

	private XStreamHelper() {
	}

	public static XStream getInstance() {
		if (xstream == null) {
			synchronized (XStream.class) {
				if (xstream == null)
					xstream = new XStream();
				Class<?>[] classes = new Class<?>[] { Definition.class, Start.class,
						End.class, Node.class, Transition.class,
						Begin.class, Exception.class, Code.class, Action.class, Arg.class, Assign.class,
						For.class, Foreach.class, Break.class, Continue.class, Fun.class,
						If.class, New.class, Logic.class, To.class,
						Create.class, Update.class, Delete.class, Query.class, Return.class,
						ExtNode.class
				};
				xstream.allowTypes(classes);
				xstream.processAnnotations(classes);
				xstream.aliasSystemAttribute(null, "class");
			}
		}
		return xstream;
	}
}
