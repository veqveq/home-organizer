import {TSMap} from "typescript-map";

export interface ItemFilter {
  commonFilter: string,
  fieldFilters: TSMap<string, any>
}
