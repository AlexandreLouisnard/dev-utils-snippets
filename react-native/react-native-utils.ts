/* #region Imports */

import { NavigationProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { Component, useEffect } from 'react';
import { AppState, AppStateStatus, BackHandler, Dimensions, StatusBar } from 'react-native';

/* #endregion */

/* #region React Native Utils */

/**
 * Returns a fonction allowing to console.log() some message/args with a given {header}.
 *
 * @export
 * @param {string} header
 * @returns {(message: string, ...args: any) => void}
 */
export function logger(header: string): (message: string, ...args: any) => void {
  return (message: string, ...args: any) => {
    if (__DEV__) {
      // eslint-disable-next-line no-console
      console.log(header, message, ...args);
    }
  };
}
// Use the logger for this Utils file too.
const log = logger('Utils');
/* #endregion */

/* #region React Native Components lifecycle */

/**
 * Interface definition for the additional {Component} lifecycle callbacks.
 *
 * @export
 * @interface ComponentLifecycle
 */
export interface ComponentLifecycle {
  onFocus(): void;
  onBlur(): void;
  onAppActive(): void;
  onAppBackground(): void;
  onAndroidBackPress(): boolean;
  lifecycleListeners?: any;
}

/**
   * Sets up some additional callbacks on a React Native {Component}.
   * Should be called during {Component#componentDidMount()}.
   * The associated {removeLifecycleListeners()} should be called during {Component#componentWillUnmount()}.
   *
  
   *
   * @export
   * @param {(ComponentLifecycle & Component<any, any>)} that
   * @param {StackNavigationProp} [navigation]
   */
export function setupLifecycleListeners(that: ComponentLifecycle & Component<any, any>, navigation?: StackNavigationProp<any, any>) {
  // onAppActive(), onAppInactive() callbacks
  const _onAppStateChange = (nextAppState: AppStateStatus) => {
    const appState: AppStateStatus = (that && that.state && that.state.appState) || null;

    if (appState && appState.match(/inactive|background/) && nextAppState === 'active') {
      that.onAppActive();
    } else if (appState === 'active' && nextAppState.match(/inactive|background/)) {
      that.onAppBackground();
    }

    that.setState({ appState: nextAppState });
  };

  const _onAndroidBackPress = () => that.onAndroidBackPress();

  // willFocus(), didFocus(), willBlur(), didBlur() callbacks
  if (navigation) {
    let changeEventSubscription;

    const onFocusCallback = navigation.addListener('focus', () => {
      // Android back press
      BackHandler.addEventListener('hardwareBackPress', _onAndroidBackPress);
      // App background/foreground
      changeEventSubscription = AppState.addEventListener('change', _onAppStateChange);
      that.onFocus();
    });

    const onBlurCallback = navigation.addListener('blur', () => {
      // Android back press
      BackHandler.removeEventListener('hardwareBackPress', _onAndroidBackPress);
      // App background/foreground
      changeEventSubscription.remove();
      that.onBlur();
    });

    that.lifecycleListeners = [onFocusCallback, onBlurCallback];
  }
}

export type LifecycleCallbacks = {
  onMount?: () => void;
  onUnmount?: () => void;
  onAppActive?: () => void;
  onAppBackground?: () => void;
  onFocus?: () => void;
  onBlur?: () => void;
  onAndroidBackPress?: () => boolean;
};
/**
 *
 * @param navigation navigation param must be provided for onFocus and onBlur. Otherwise it can be skipped.
 * @param callbacks
 */
export const useLifecycleListeners = (callbacks: LifecycleCallbacks, navigation?: NavigationProp<any, any>) => {
  const { onMount, onUnmount, onAppActive, onAppBackground, onFocus, onBlur, onAndroidBackPress } = callbacks;

  useEffect(() => {
    const eventSubscription = AppState.addEventListener('change', _onAppStateChange);
    onMount?.();
    return () => {
      eventSubscription.remove();
      onUnmount?.();
    };
  }, []); // empty square brackets means that this effect will run only once

  useEffect(() => {
    if (!navigation) {
      return () => {};
    }

    const focusListener = navigation.addListener('focus', () => {
      if (onAndroidBackPress) {
        BackHandler.addEventListener('hardwareBackPress', onAndroidBackPress);
      }
      onFocus?.();
    });
    const blurListener = navigation.addListener('blur', () => {
      if (onAndroidBackPress) {
        BackHandler.removeEventListener('hardwareBackPress', onAndroidBackPress);
      }
      onBlur?.();
    });

    // Return the function to unsubscribe from the event so it gets removed on unmount
    return () => {
      focusListener();
      blurListener();
    };
  }, [navigation]);

  const _onAppStateChange = (nextAppState: AppStateStatus) => {
    if (nextAppState === 'active') {
      onAppActive?.();
    } else if (nextAppState.match(/inactive|background/)) {
      onAppBackground?.();
    }
  };
};

/**
 * Unsets the additional callbacks on a React Native {Component}.
 * Should be called during {Component#componentWillUnmount()}.
 *
 * @export
 * @param {(ComponentLifecycle & Component<any, any>)} that
 */
export function removeLifecycleListeners(that: ComponentLifecycle & Component<any, any>) {
  // Will/Did Blur/Focus lifecycle3
  if (that.lifecycleListeners) {
    that.lifecycleListeners.forEach((sub: any) => (sub.remove ? sub.remove() : null));
  }
}

/* #endregion */

/* #region React Native UI */

/**
 * Returns the device screen dimensions.
 *
 * @export
 * @returns	{{
 *	 		 	screenHeight: number,	// the total screen height
 * 				screenWidth: number,	// the total screen width
 * 				headerHeight: number,	// the header (application toolbar) height
 * 				toolbarHeight: number	// the toolbar (system) height
 * 			}}
 */
export const useScreenDimensions = () => {
  const { height: screenHeight, width: screenWidth } = Dimensions.get('window');

  const toolbarHeight = StatusBar.currentHeight || 20;
  const headerHeight = 0; // TODO: useHeaderHeight();
  const screenUtilHeight = screenHeight - toolbarHeight - headerHeight;

  return { screenHeight, screenWidth, headerHeight, toolbarHeight, screenUtilHeight };
};

/* #endregion */
