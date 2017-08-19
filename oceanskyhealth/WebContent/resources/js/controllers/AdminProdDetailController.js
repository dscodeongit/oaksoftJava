'use strict';

/**
 * CarController
 * @constructor
 */
App.controller('AdminProdDetailController', ['CatService', '$state', '$stateParams', function( CatService, $state, $stateParams) {
	var self = this; 
	self.prod = {};

	self.selectedPcat = {};
	self.selectedCat = {};
	
	if($stateParams && $stateParams.prd){		
		self.prod = $stateParams.prd;	
		//alert('$stateParams.prd=' + $stateParams.prd);
		if($stateParams.idx >= 0 ){
			self.index=$stateParams.idx;
			console.log('DetailController:$stateParams.idx=' + $stateParams.idx);
		}
	}
	//self.isAdmin = true;
	self.fetchCategory = function() {
    	self.pcats = CatService.getService('fetchAll').query(    
		    function(result, responseHeaders){
		        console.log('Total cats received', result.length);
		    }, function(httpResponse){
		        console.log('Error while fetching category tree');
		    });
    };        
    
    self.editProd = function() {
        $state.go('prodEdit', {prd:self.prod, idx:self.index});
    };
  
    self.fetchCategory();
      
}]);