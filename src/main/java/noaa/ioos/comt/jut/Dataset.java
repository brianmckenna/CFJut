package noaa.ioos.comt.jut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class Dataset {
	
	private NetcdfDataset ncd = null;

    public Dataset(String fileName) {
		
		try {
			this.ncd = NetcdfDataset.openDataset(fileName);
		} catch (Exception e) {
			// not a NetCDF file
		}
		finally {
			try {
				this.ncd.close();
			} catch (Exception e) {
				// pass
			}
		}

    }
    
    public String json() {
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	Group group = this.ncd.getRootGroup();
    	
    	//List<EnumTypedef> enumTypedefs = group.getEnumTypedefs();
    	List<Dimension> dimensions = group.getDimensions();
    	List<Variable> variables = group.getVariables();
    	//List<Group> groups = group.getGroups();
    	List<Attribute> attributes = group.getAttributes();
    	
        if (dimensions.size() > 0) {
        	Map<String, Object> dimensionsMap = new HashMap<String, Object>();
        	for(Dimension dimension : dimensions) {
        		dimensionsMap.put(dimension.getFullName(), dimension.getLength());
        	}
        	map.put("dimensions", dimensionsMap);
        }

        if (variables.size() > 0) {
        	Map<String, Object> variablesMap = new HashMap<String, Object>();
        	for(Variable variable : variables) {
        		variablesMap.put(variable.getFullName(), variable.getDimensionsString());
        	}
        	map.put("variables", variablesMap);
        }

        if (attributes.size() > 0) {
        	for(Attribute attribute : attributes) {
        		map.put(attribute.getFullName(), attribute.getValue(0).toString());
        	}
        }
        
        //Gson gson = new Gson(); // for PROD
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(map); 
		return json;
    	
    }
    
}