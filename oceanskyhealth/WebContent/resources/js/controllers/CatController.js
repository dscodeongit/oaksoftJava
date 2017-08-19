'use strict';

/**
 * CatController
 * @constructor , 'CatService'
 */
App.controller('CatController', ['CatService', function(CatService,  $scope) {
	var self = this;    
    self.editMode = false;	
    self.selectedPcat = {};
    self.createPcat = {};
    self.createCat = {};
    
    self.pcats = CatService.getAllCats();
    		    
    self.addNewCat = function(name) { 
        var cat = {};
        cat.name = name;
        var pcat = {};
        pcat.name = self.selectedPcat.name;
        cat.parent = pcat;
        //alert("add new cat ... " + cat); 
        //CatService.saveCat(cat); 
        CatService.getService('saveCat').save(cat, function(){ 
        	 self.selectedPcat = {};
             self.createCat = {};
             self.pcats = CatService.getAllCats();
            console.log('Cat saved');
        },function(){
            console.log('Cat could not be saved');
        });    	
       
    };
    
    self.addNewPcat = function(name) {
        var pcat = {};
        pcat.name = name;
        //alert("add new pcat ... " + name); 
        //CatService.savePcat(pcat);       
        CatService.getService('savePcat').save(pcat, function(){ 
        	self.createPcat = {};
            self.pcats = CatService.getAllCats();
            console.log('Cat saved');
        },function(){
            console.log('Cat could not be saved');
        });
        
    };
    
    self.removeCat = function(name) {
    	
    	CatService.getService('removeCat/'+name).delete(
			function(result, responseHeaders){
				if(!result.successful){
		    		//alert('cat remove - prams! result: ' + result);
					self.setError(result.messageKey);
				}else{
					self.pcats = CatService.getAllCats ();
				}
	        //console.log('Total cats received', result.length);
		    }, function() {
	    		//alert('callback from cat remove!');
	    	});		       
    	
    };
    
    self.removePcat = function(name) {
    	CatService.getService('removePcat/'+name).delete(
    			function(result, responseHeaders){
    				if(!result.successful){
    		    		//alert('cat remove - prams! result: ' + result);
    					self.setError(result.messageKey);
    				}else{
    					self.pcats = CatService.getAllCats ();
    				}
    	        //console.log('Total cats received', result.length);
    		    }, function() {
    	    		//alert('callback from cat remove!');
    	    	});		
    };
    
    self.resetError = function() {
    	self.error = false;
    	self.errorMessage = '';
    };

    self.setError = function(message) {
    	self.error = true;
    	self.errorMessage = message;
    };
    
    self.squre = function(){
    	//self.answer = CalculatorService.square(self.number);
    }
    self.cube = function(){
    	//self.answer = CalculatorService.cube(self.number);
    }
    
}]);