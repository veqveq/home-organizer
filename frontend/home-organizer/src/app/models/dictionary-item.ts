import {TSMap} from "typescript-map";

export interface DictionaryItem {
  id: string,

  dictionaryId?: string
  fieldValues: TSMap<string, any>
}
