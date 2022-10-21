export interface DictionaryField {
  id?: string,
  position: number,
  name: string,
  type: string,
  unique?: boolean,
  required?: boolean,
  defaultValue?: string
}
