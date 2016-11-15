package codemagic.util.shared.common.contract;

import java.io.Serializable;

public interface HasSetId extends Serializable {
	
	void setId(Long id);
}
