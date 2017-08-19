package com.oaksoft.logging.config;

import java.util.Collection;

public interface Monitor {
	Collection<DataSnap> snap(Object connectionObject) throws Exception;
}
