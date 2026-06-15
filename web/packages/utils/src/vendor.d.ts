declare module 'crypto-js/sha1' {
  const sha1: (value: string) => { toString(): string };
  export default sha1;
}
