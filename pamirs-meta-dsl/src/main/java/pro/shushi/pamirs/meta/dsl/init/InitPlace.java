package pro.shushi.pamirs.meta.dsl.init;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import pro.shushi.pamirs.meta.dsl.exception.InitialException;
import pro.shushi.pamirs.meta.dsl.exception.Initializable;
import pro.shushi.pamirs.meta.dsl.definition.helper.DefinitionHelper;

@Component
public class InitPlace implements Initializable {

	public void initialise() throws InitialException {
		Resource[] processResources = ConfigServerHolder.getProcessLocations();
		if(null != processResources){
			DefinitionHelper.deployProcessFolder(processResources);
		}
	}

}
