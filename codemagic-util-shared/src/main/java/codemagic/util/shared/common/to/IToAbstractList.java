package codemagic.util.shared.common.to;

import java.io.Serializable;
import java.util.List;

public interface IToAbstractList<T> extends Serializable{

	/** The data returned by the server */
	public abstract List<T> getData();

	public abstract void setData(List<T> data);

}