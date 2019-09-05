(function(responsiveVoice) {
    'use strict';

    function TtsService($http) {
        var service = {};

        service.init = function(){
            responsiveVoice.setDefaultVoice("Korean Female");

        };

        service.speak = function(text){
            if(responsiveVoice.voiceSupport()){
                console.log("Responsive Voice Supported");
                responsiveVoice.speak(text);
                console.debug("Saying: " + text);
            }
        };

        return service;
    }

    angular.module('SmartMirror')
        .factory('TtsService', TtsService);

}(window.responsiveVoice));