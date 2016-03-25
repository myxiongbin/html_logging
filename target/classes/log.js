	var wsServer = "ws://192.168.25.158:38080/websockets/logger"; 
	var websocket = null;
	
	try{
		websocket = new WebSocket(wsServer); 
		
		websocket.onopen = function (evt) { 
			onOpen(evt) 
		}; 
		
		websocket.onclose = function (evt) { 
			onClose(evt) 
		}; 
		
		websocket.onmessage = function (evt) { 
			onMessage(evt) 
		}; 
		
		websocket.onerror = function (evt) { 
			onError(evt) 
		}; 
	}catch(e){
		websocket = null;
	}
	
	function onOpen(evt) { 
		console.log("Connected to WebSocket server."); 
	} 
	function onClose(evt) { 
		console.log("Disconnected"); 
	} 
	function onMessage(evt) { 
//		console.log('Retrieved data from server: ' + evt.data); 
	} 
	function onError(evt) { 
		console.log('Error occured: ' + evt.data); 
	}
	
	//日志发送
	function send(log){
		if(DEBUG){
			log.type = navigator.userAgent
	
			if(websocket != null){
				try{
					websocket.send(JSON.stringify(log));
				}catch(e){
					
				}
			}
		}
	}
	
	console.log = function(log) {
		var json = {};
		json.grade = "log";
		json.message = log;
		send(json);
	}
	
	console.error = function(log) {
		var json = {};
		json.grade = "error";
		json.message = log;
		send(json);
	}
	
	console.info = function(log) {
		var json = {};
		json.grade = "info";
		json.message = log;
		send(json);
	}
	
	console.warn = function(log) {
		var json = {};
		json.grade = "warn";
		json.message = log;
		send(json);
	}
	
	window.onerror = function(errorMessage,scriptURI,lineNumber,columnNumber){
		reportError({
			message: errorMessage,
			script: scriptURI,
			line: lineNumber,
			column: columnNumber
		});
	}
	
	function reportError(error){
		console.error(JSON.stringify(error));
	}
	
	/**
	 * 日志工厂
	 */
	var logger = function(window){
		return {
			getLogger : function(name){
				return new _logger(name);
			}
		};
	}(typeof window === 'undifined' ? this: window);

	/**
	 * 日志类
	 */
	function _logger(name){
		this.name = name;
		
		/**
		 * 日志级别:info
		 */
		this.info = function(log) {
			var json = {};
			json.grade = "info";
			json.message = log;
			json.name = this.name;
			send(json);
	    };
	    
	    /**
		 * 日志级别:info
		 */
	    this.log = function(log) {
			var json = {};
			json.grade = "log";
			json.message = log;
			json.name = this.name;
			send(json);
		}

	    /**
		 * 日志级别:error
		 */
	    this.error = function(log) {
			var json = {};
			json.grade = "error";
			json.message = log;
			json.name = this.name;
			send(json);
		}

	    /**
		 * 日志级别:warn
		 */
	    this.warn = function(log) {
			var json = {};
			json.grade = "warn";
			json.message = log;
			json.name = this.name;
			send(json);
		}
	}
