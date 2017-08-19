'use strict';

/* Directives */

var AppDirectives = angular.module('App.directives', []);

AppDirectives.directive('appVersion', ['version', function (version) {
    return function (scope, elm, attrs) {
        elm.text(version);
    };
}]);

AppDirectives.directive("redstar", function() {
    return {
        restrict: 'A', // only for attributes
        compile: function(element) {
            // insert asterisk after elment 
            element.after("<span class='redstar'>*</span>");
        }
    };
});

AppDirectives.directive( "mwConfirmClick", 
	 function( ) {
	   return {
	     priority: -1,
	     restrict: 'A',
	     scope: { confirmFunction: "&mwConfirmClick" },
	     link: function( scope, element, attrs ){
	       element.bind( 'click', function( e ){
	         // message defaults to "Are you sure?"
	         var message = attrs.mwConfirmClickMessage ? attrs.mwConfirmClickMessage : "Are you sure?";
	         // confirm() requires jQuery
	             if( confirm( message ) ) {
	               scope.confirmFunction();
	             }
	           });
	         }
	       }
	     }
	   );

AppDirectives.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);
