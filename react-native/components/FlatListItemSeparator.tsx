import React from 'react';
import { View, ViewStyle } from 'react-native';

export const FlatListItemSeparator = ({ style, ...otherProps }: { style?: ViewStyle }) => (
  <View
    style={{
      height: 1,
      backgroundColor: '#CED0CE',
      marginLeft: 2,
      marginRight: 2,
      ...style,
    }}
    {...otherProps}
  />
);
FlatListItemSeparator.defaultProps = {
  style: {},
};
