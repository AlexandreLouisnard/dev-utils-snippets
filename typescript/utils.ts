/* #region Imports */
import { Buffer } from 'buffer';
/* #endregion */


/* #region Javascript */
/**
 * Returns a deep copy/clone of @param src .
 *
 * @param src the object to deep copy
 * @returns a deep copy of @param src
 */
export function deepCopy<T>(src: T): T {
  let target: T;
  if (Buffer.isBuffer(src)) {
    target = Buffer.from(src) as unknown as T;
  } else if (src instanceof Map) {
    target = new Map(src) as T;
  } else {
    target = (Array.isArray(src) ? [] : {}) as T;
    for (let prop in src) {
      let value = src[prop];
      if (value && typeof value === 'object') {
        target[prop] = deepCopy(value);
      } else {
        target[prop] = value;
      }
    }
  }
  return target;
}

/**
 * Returns a deep copy/clone of @param src .
 *
 * NOTE: works only for serializable objects. Non-serializable properties will be skipped.
 *
 * @param src the serializable object to deep copy
 * @returns a deep copy of @param src
 */
export function deepCopyForSerializableObjects<T>(src: T): T {
  return JSON.parse(JSON.stringify(src));
}

/**
 * Sleeps for the given amount of time, in milliseconds. To be used in an async function such as:
 *
 * async function demo() {
 *  console.log('Start');
 *  await sleep(2000);
 *  console.log('Two seconds later');
 * }
 *
 * @param ms
 * @returns
 */
export function sleep(ms: number) {
  return new Promise<void>((resolve) => global.setTimeout(resolve, ms));
}

/**
 * Returns a unique hash code for the given string.
 *
 * @param str the string to hash
 * @returns the hash code as a 32 bit integer
 */
export function hashCode(str: string): number {
  let hash = 0;
  let i;
  let chr;

  if (str.length === 0) return hash;
  for (i = 0; i < str.length; i++) {
    chr = str.charCodeAt(i);
    hash = (hash << 5) - hash + chr;
    hash |= 0; // Convert to 32 bit integer
  }
  return hash;
}

export type EqualityCheck = 'X_EQUALS_Y' | 'X_SUBSET_OF_Y';

/**
 * Depending on {equalityCheck}, indicates whether:
 * * 'X_EQUALS_Y' (default): {x} and {y} are equal or have the same properties and the same properties values.
 * * 'X_SUBSET_OF_Y': {x} is a subset of {y}: {x} properties are included in {y} with the same values.
 *
 * @export
 * @param {*} x
 * @param {*} y
 * @param {EqualityCheck} [equalityCheck='X_EQUALS_Y']
 * @returns {boolean}
 */
export function equals(x: any, y: any, equalityCheck: EqualityCheck = 'X_EQUALS_Y'): boolean {
  if (x === y) {
    return true;
  }
  if (x === null || x === undefined || y === null || y === undefined) {
    return x === y;
  }
  // after this just checking type of one would be enough
  if (x.constructor !== y.constructor) {
    return false;
  }
  // if they are functions, they should exactly refer to same one (because of closures)
  if (x instanceof Function) {
    return x === y;
  }
  // if they are regexps, they should exactly refer to same one (it is hard to better equality check on current ES)
  if (x instanceof RegExp) {
    return x === y;
  }
  if (x === y || x.valueOf() === y.valueOf()) {
    return true;
  }
  if (Array.isArray(x)) {
    switch (equalityCheck) {
      case 'X_EQUALS_Y':
        if (x.length !== y.length) {
          return false;
        }

        break;
      case 'X_SUBSET_OF_Y':
        if (x.length > y.length) {
          return false;
        }

        break;
      default:
        // log(`Utils: equals(): ERROR: unknown equality check: ${equalityCheck}`);
        return false;
    }
  }

  // if they are dates, they must had equal valueOf
  if (x instanceof Date) {
    return false;
  }

  // if they are strictly equal, they both need to be object at least
  if (!(x instanceof Object)) {
    return false;
  }
  if (!(y instanceof Object)) {
    return false;
  }

  // recursive object equality check
  const xKeys = Object.keys(x);
  switch (equalityCheck) {
    case 'X_EQUALS_Y':
      return (
        Object.keys(y).every((yKey) => xKeys.indexOf(yKey) !== -1) &&
        xKeys.every((xKey) => equals(x[xKey], y[xKey], equalityCheck))
      );
    case 'X_SUBSET_OF_Y':
      return xKeys.every((xKey) => equals(x[xKey], y[xKey], equalityCheck));
    default:
      // log(`equals(): ERROR: unknown equality check: ${equalityCheck}`);
      return false;
  }
}

/**
 * Returns the value of a nested property or a default value if some of the property chain is undefined.
 *
 * Example: get(test, ['nested', 'property', 2, 'prop'], 'default value') will return test.nested.property[2].prop if all the chain is defined, or 'default value'.
 *
 * NOTE: with TypeScript, this method is useless as we can use optional chaining instead: test?.nested?.property?.[2]?.prop ?? 'default value'
 */
export const get: <T = any>(object: any, pathToNestedProperty: (string | number)[], defaultValue?: T) => T = (
  object,
  pathToNestedProperty,
  defaultValue = undefined,
) => pathToNestedProperty.reduce((xs, x) => (xs && xs[x] !== undefined ? xs[x] : defaultValue), object);

/**
 * TS type definition for an object with an {id} attribute.
 */
export type IDable = { id: any };

/**
 * Returns a new array in which any item such as {item.id === itemId} has been updated by the updateItemCallback(item) function, or the same array if nothing has been updated.
 *
 * This function is mutating/non-mutating depending on @param updateItemCallback() being mutating/non-mutating.
 *
 * @export
 * @param {Array} array
 * @param {*} itemId
 * @param {*} updateItemCallback
 * @returns
 */
export function updateItemInArray<X>(array: (X & IDable)[], itemId: any, updateItemCallback: (item: X) => X): X[] {
  const updatedItems: X[] = array.map<X>((item) => {
    if (equals(item.id, itemId)) {
      // Use the provided callback to create an updated item
      const updatedItem = updateItemCallback(item);
      return updatedItem;
    }
    // Since we only want to update one item, preserve all others as they are now
    return item;
  });

  return updatedItems;
}

/**
 * Returns a new array in which either:
 * - Any item such as {item.id === itemId} has been replaced by {newItem}.
 * - {newItem} has been added;
 *
 * @export
 * @template X
 * @param {((X & IDable)[])} array
 * @param {(X & IDable)} newItem
 * @returns {X[]}
 */
export function addOrReplaceItemInArray<X>(array: (X & IDable)[], newItem: X & IDable): X[] {
  let replaced: boolean = false;
  const updatedItems: X[] = array.map<X>((item) => {
    if (equals(item.id, newItem.id)) {
      // Replace item
      replaced = true;
      return newItem;
    }
    return item;
  });
  if (!replaced) {
    // New item
    updatedItems.push(newItem);
  }

  return updatedItems;
}

/**
 * Indicates whether the given @param object is a @type Promise.
 *
 * @export
 * @param object
 * @returns
 */
export function isPromise(object: any): boolean {
  if (Promise && Promise.resolve) {
    return Promise.resolve(object) === object;
  }
  throw new Error('Promise not supported in your environment');
}

/* #endregion */

/* #region Format/convert/check strings/units/data */

/**
 * Returns the formatted MAC address with the given {divisionChar}. For example if divisionChar = ":", MAC = "01:AA:22:33:BB:44".
 * @param rawMac a raw MAC address (12 alphanum chars) such as 01AA2233BB44
 * @param divisionChar a division character such as ':', '.', '-', etc
 * @return the formatted MAC address {string} or an empty {string}
 */
export function formatMacAddress(rawMac: string, divisionChar: string): string {
  let macStr = '';
  if (rawMac.length === 12) {
    macStr = rawMac
      .replace(/(.{2})/g, `$1${divisionChar}`)
      .substring(0, 17)
      .toLocaleUpperCase();
    if (macStr.length !== 17) {
      macStr = '';
    }
  }
  return macStr;
}

/**
 * Returns the normalized MAC address {string} (12 alphanum chars) matching this format: 01AA2233BB44.
 * @param formattedMac a formatted MAC address such as 01:AA:22:33:BB:44, 01.AA.22.33.BB.44, 01aa2233bb44.
 * @return the normalized MAC address {string} or <b>null</b>
 */
export function normalizeMacAddress(formattedMac: string): string {
  const mac: string = formattedMac.replace(/[^a-fA-F0-9]/, '').toLocaleUpperCase();
  if (mac.length !== 12) {
    throw new Error(`normalizeMacAddress(): could not normalize MAC address: ${formattedMac}`);
  }
  return mac;
}

/**
 * Indicates whether the given {string} is a valid MAC address.<br/>
 * Are considered valid: 01AA2233BB44, 01:AA:22:33:BB:44, 01.AA.22.33.BB.44, 01aa2233bb44, etc.
 * @param mac
 * @return
 */
export function checkMacAddressValidity(mac: string): boolean {
  if (
    mac.length !== 12 &&
    /* raw MAC */
    mac.length !== 17
    /* MAC with separator */
  ) {
    return false;
  }
  return /^([a-fA-F0-9]{2}[.:-]?){5}[a-fA-F0-9]{2}$/.test(mac);
}

/**
 * Formats the given {Date} as a "YYYY-MM-DD HH:MM:SS" {string}.
 *
 * @export
 * @param {?Date} date
 * @returns {string}
 */
export function dateToYYYYMMDDHHMMSS(date: Date | null | undefined): string {
  if (!date) {
    return '';
  }
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(
    2,
    '0',
  )} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(
    date.getSeconds(),
  ).padStart(2, '0')}`;
}

/**
 * Indicates whether the given {string} looks like a phone number (permissive check...).
 *
 * @export
 * @param {?string} phone
 * @returns {boolean}
 */
export function isPhoneNumber(phone: string | null | undefined): boolean {
  if (!phone) {
    return false;
  }
  return phone.match(/^(\+\d{1,4})?(\(\d{1,3}\))?\d{7,13}$/) !== null;
}

/**
 * Strips out forbidden JSON characters from the given {string}, such as |, &, ;, $, %, @, ", <, >, (, ), +, ,, #, [, ], ., \, /, etc.
 *
 * @export
 * @param {string} key
 * @returns {string}
 */
export function removeForbiddenJsonKeyCharacters(key: string): string {
  return key.replace(/[\|\&\;\$\%\@\"\<\>\(\)\+\,\#\[\]\.\\\/]/g, '_'); // eslint-disable-line no-useless-escape
}

/* #endregion */

/* #region Binary */

/**
 * For the given {@code number}, sets the bit at {@code bitIndex} to {@code bitValue}, and returns the new number value.
 *
 * @export
 * @param number
 * @param bitIndex
 * @param bitValue
 * @returns
 */
export function setBit(number: number, bitIndex: number, bitValue: number): number {
  switch (bitValue) {
    case 0:
      return number & ~(1 << bitIndex);
    case 1:
      return number | (1 << bitIndex);
    default:
      return number;
  }
}

/* #endregion */
