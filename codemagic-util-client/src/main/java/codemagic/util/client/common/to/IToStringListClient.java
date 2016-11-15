package codemagic.util.client.common.to;

import codemagic.util.shared.common.to.IToAbstractList;

import com.google.gwt.query.client.builders.JsonBuilder;

/**
 * Used with GQuery to get List{@literal<}String{@literal>} from server.
 */
public interface IToStringListClient extends IToAbstractList<String>, JsonBuilder {}
