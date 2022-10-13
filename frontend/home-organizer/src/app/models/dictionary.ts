import {DictionaryField} from "./dictionary-field";

export interface Dictionary {
  id?: string,
  name: string
  fields?: DictionaryField[]
}
