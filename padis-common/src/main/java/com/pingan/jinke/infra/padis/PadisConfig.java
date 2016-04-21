package com.pingan.jinke.infra.padis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static com.pingan.jinke.infra.padis.common.Constant.*;

@NoArgsConstructor
@Setter
@Getter
public class PadisConfig {



	private String instance;
	
	private String nameSpace;
	
	private String zkAddr;
	
	private int maxRedirections = DEFAULT_MAX_DIRECTION;
	
	private int connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
	
	private int soTimeout = DEFAULT_SO_TIMEOUT;	
	
    private int maxTotal = DEFAULT_MAX_TOTAL;

    private int maxIdle = DEFAULT_MAX_IDLE;

    private int minIdle = DEFAULT_MIN_IDLE;
    
    
}