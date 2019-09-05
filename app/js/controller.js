(function (angular) {
	'use strict';

	function MirrorCtrl(
		Focus,
		TtsService,
		SpeechService,
		AutoSleepService,
		LightService,
		$rootScope, $scope, $timeout, $interval, tmhDynamicLocale, $translate) {

		TtsService.init();
		// Local Scope Vars
		var _this = this;
		var command = COMMANDS.ko;
		var functionService = FUNCTIONSERVICE;
		var DEFAULT_COMMAND_TEXT = command.default;
		$scope.listening = false;
		$scope.debug = false;
		$scope.commands = COMMANDS.ko;
		$scope.partialResult = $translate.instant('home.commands');
		$scope.layoutName = 'main';
		$scope.config = config;


		// Set up our Focus
		$rootScope.$on('focus', function (targetScope, newFocus) {
			$scope.focus = newFocus;
		})

		Focus.change("default");

		//set lang
		if (config.general.language.substr(0, 2) == 'en') {
			moment.locale(config.general.language,
				{
					calendar: {
						lastWeek: '[Last] dddd',
						lastDay: '[Yesterday]',
						sameDay: '[Today]',
						nextDay: '[Tomorrow]',
						nextWeek: 'dddd',
						sameElse: 'L'
					}
				}
			)
		} else {
			moment.locale(config.general.language)
		}
		//Initialize the speech service

		var resetCommandTimeout;
		SpeechService.init({
			listening: function (listening) {
				$scope.listening = listening;
				if (listening && !AutoSleepService.woke) {
					AutoSleepService.wake()
					$scope.focus = AutoSleepService.scope;
				}
			},
			partialResult: function (result) {
				$scope.partialResult = result;
				$timeout.cancel(resetCommandTimeout);
			},
			finalResult: function (result) {
				if (typeof result !== 'undefined') {
					$scope.partialResult = result;
					resetCommandTimeout = $timeout(restCommand, 5000);
				}
			},
			error: function (error) {
				console.log(error);
				if (error.error == "network") {
					$scope.speechError = "Google Speech Recognizer: Network Error (Speech quota exceeded?)";
				}
			}
		});
		//Update the time
		function updateTime() {
			$scope.date = new moment();

			// Auto wake at a specific time

			if (typeof config.autoTimer !== 'undefined' && config.autoTimer.mode !== 'disabled' && typeof config.autoTimer.autoWake !== 'undefined' && config.autoTimer.autoWake == moment().format('HH:mm:ss')) {
				console.debug('Auto-wake', config.autoTimer.autoWake);
				AutoSleepService.wake()
				$scope.focus = AutoSleepService.scope;
				AutoSleepService.startAutoSleepTimer();
			}
		}

		// Reset the command text
		var restCommand = function () {
			$translate('home.commands').then(function (translation) {
				$scope.partialResult = translation;
			});
		};

		_this.init = function () {
			AutoSleepService.startAutoSleepTimer();

			$interval(updateTime, 1000);
			updateTime();
			restCommand();

			var defaultView = function () {
				console.debug("Ok, going to default view...");
				Focus.change("default");
			}

			// List commands
			SpeechService.addCommand('list', function () {
				console.debug("Here is a list of commands...");
				console.log("사용가능한질문 입력들어옴");
				console.log(SpeechService.commands);
				$scope.commands.commandPage = []
				$scope.commands.commandPage = SpeechService.getCommands();
				$scope.commands.index = 0
				$scope.commands.totalPages = $scope.commands.commandPage.length
				Focus.change("commands");
				TtsService.speak("다음은 이용 가능한 메뉴입니다.");
				//$scope.focus = "commands";
			});

			SpeechService.addCommand('list-page', function (pageNum) {
				if (Focus.get() == 'commands') {
					$scope.commands.commandPage = []
					$scope.commands.commandPage = SpeechService.getCommands();
					$scope.commands.totalPages = $scope.commands.commandPage.length
					if (isNaN(pageNum)) {
						pageNum = $scope.units[pageNum]
					}
					if ( pageNum >= 1 && pageNum <= ($scope.commands.totalPages)) {
						$scope.commands.index = pageNum-1
					}
				}
			})

			// Next Page
			SpeechService.addCommand('list-next', function () {
				if (Focus.get() == 'commands') {
					$scope.commands.commandPage = []
					$scope.commands.commandPage = SpeechService.getCommands();
					$scope.commands.totalPages = $scope.commands.commandPage.length
					if ($scope.commands.index < ($scope.commands.totalPages - 1)) {
						$scope.commands.index++
					}
				}
				TtsService.speak("다음 목록 입니다.");
			})

			// Prev Page
			SpeechService.addCommand('list-prev', function () {
				if (Focus.get() == 'commands') {
					$scope.commands.commandPage = []
					$scope.commands.commandPage = SpeechService.getCommands();
					$scope.commands.totalPages = $scope.commands.commandPage.length
					if ($scope.commands.index > 0) {
						$scope.commands.index--
					}
				}
			})

			// Go back to default view
			SpeechService.addCommand('home', function(){
				console.debug("Ok, going to default view...");
				Focus.change("default");
				TtsService.speak("홈으로 이동합니다.")
			});

			SpeechService.addCommand('play_video',function(){
				console.debug("가이드 영상을 재생합니다");
				$.ajax({
					type: "POST",
					url: "~/sendData.py",
					data: {param: 1}
				}).done(function(o){
					console.log("python success");
				}).error(function(o){
					console.log("python fail");
				})
				TtsService.speak("가이드 영상을 재생합니다.");
				Focus.change("default");
			});
			SpeechService.addCommand('debug', function () {
				console.debug("Boop Boop. Showing debug info...");
				$scope.debug = true;
			});

			// Check the time
			SpeechService.addCommand('time_show', function () {
				console.debug("It is", moment().format('h:mm:ss a'));
			});

			// Control light
			SpeechService.addCommand('light_action', function (state, target, action) {
				LightService.performUpdate([state, target, action].join(" "));
			});
		};

		_this.init();
	}

	angular.module('SmartMirror')
		.controller('MirrorCtrl', MirrorCtrl);

	function themeController($scope) {
		$scope.layoutName = (typeof config.layout !== 'undefined' && config.layout) ? config.layout : 'main';
	}

	angular.module('SmartMirror')
		.controller('Theme', themeController);

} (window.angular));
