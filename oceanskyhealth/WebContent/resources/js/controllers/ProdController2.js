'use strict';

/**
 * CarController
 * @constructor
 */
var ProdController = function($scope, $http) {
	$scope.prod = {};
	$scope.editMode = false;	
	$scope.selectedPcat = {}
	$scope.selectedCat = {}
	$scope.fetchCategory = function() {
        $http.get('products/pcats.json').success(function(pcatList){
            $scope.pcats = pcatList;           
        });
    };
    
    $scope.fetchProdList = function() {
        $http.get('products/products.json').success(function(prodList){
        	angular.forEach(prodList, function(prod, index) {
           	    if(prod.tags.indexOf("NEW") >=0 ){
           	    	prod.isNew = true;
           	    }           	    
	           	if(prod.tags.indexOf("HOT") >=0 ){
	       	    	prod.isHot = true;
	       	    }
           	});
            $scope.products = prodList;           
        });
        $scope.fetchCategory();
    };
            
    $scope.fetchProdCount = function() {
        $http.get('products/prodcount.json').success(function(count){
            $scope.prodcount = count;
        });
    };
    
    $scope.saveProd = function(prod) {
    	 $scope.resetError();
         prod.tags = [];
         prod.cat = $scope.selectedCat;
         $scope.selectedPcat.cats = null; 
         prod.cat.parent = $scope.selectedPcat;
         
         if(prod.isNew){
             prod.tags.push("NEW");
         }
         delete prod.isNew;   
         if(prod.isHot){
             prod.tags.push("HOT");
         }
         delete prod.isHot;   

         $http.post('products/saveProduct/', prod).success(function() {
             $scope.fetchProdList();
         }).error(function() {
             $scope.setError('Could not add/update product');
         });
         $scope.prod = {};
    };
    
    $scope.addNewProd = function(prod) {
        $scope.saveProd(prod);
    };

    $scope.updateProd = function(prod) {
        //alert("update prod ... ");    
    	$scope.saveProd(prod);
    };
    
    $scope.removeProd = function(prodNo) {
        alert("delete prod ... ");    
        $http.delete('products/removeProduct/'+ prodNo).success(function() {
            $scope.fetchProdList();
        }).error(function() {
            $scope.setError('Could not delete product');
        });
        $scope.prod = {};
    };
    
    $scope.uploadFile = function ($scope, Upload, $timeout) {
        $scope.$watch('files', function () {
            $scope.upload($scope.files);
        });
        $scope.$watch('file', function () {
            if ($scope.file != null) {
                $scope.files = [$scope.file]; 
            }
        });
        $scope.log = '';

        $scope.upload = function (files) {
            if (files && files.length) {
                for (var i = 0; i < files.length; i++) {
                  var file = files[i];
                  if (!file.$error) {
                    Upload.upload({
                        url: 'https://angular-file-upload-cors-srv.appspot.com/upload',
                        data: {
                          username: $scope.username,
                          file: file  
                        }
                    }).then(function (resp) {
                        $timeout(function() {
                            $scope.log = 'file: ' +
                            resp.config.data.file.name +
                            ', Response: ' + JSON.stringify(resp.data) +
                            '\n' + $scope.log;
                        });
                    }, null, function (evt) {
                        var progressPercentage = parseInt(100.0 *
                        		evt.loaded / evt.total);
                        $scope.log = 'progress: ' + progressPercentage + 
                        	'% ' + evt.config.data.file.name + '\n' + 
                          $scope.log;
                    });
                  }
                }
            }
        };
    }
    
    $scope.editProd = function(prod) {
        $scope.resetError();
        $scope.prod = prod;
        $scope.selectedPcat = prod.cat.parent;
    	$scope.selectedCat = prod.cat;
        $scope.editMode = true;
    };
    
    $scope.resetProdForm = function() {
        $scope.resetError();
        $scope.prod = {};
        $scope.selectedCat = {};
        $scope.selectedPcat = {};
        $scope.editMode = false;
    };
    
    $scope.setProdCat = function(cat){
    	$scope.prod.cat = cat;
    	$scope.prod.cat.parent = $scope.selectedPcat
    };
    
    $scope.resetError = function() {
        $scope.error = false;
        $scope.errorMessage = '';
    };

    $scope.setError = function(message) {
        $scope.error = true;
        $scope.errorMessage = message;
    };
    
    $scope.fetchProdList();
};