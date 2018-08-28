package cz.metacentrum.perun.core.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Pavel Vyskocil <vyskocilpavel@muni.cz>
 */
public class Subject extends PerunBean implements Comparable<PerunBean>{
	protected ExtSource extSource;
	protected Map<String, String> mapOfAttribute;

	public Subject(){
		super();
	}

	public Subject(ExtSource extSource, Map<String, String> mapOfAttribute) {
		super();
		this.extSource = extSource;
		this.mapOfAttribute = mapOfAttribute;
	}

	public ExtSource getExtSource() {
		return extSource;
	}

	public void setExtSource(ExtSource extSource) {
		this.extSource = extSource;
	}

	public Map<String, String> getMapOfAttribute() {
		return mapOfAttribute;
	}

	public void setMapOfAttribute(Map<String, String> mapOfAttribute) {
		this.mapOfAttribute = mapOfAttribute;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": ExtSource='" + extSource.toString() + "', MapOfAttribute='" + mapOfAttribute.toString() + "'.";
	}
}
