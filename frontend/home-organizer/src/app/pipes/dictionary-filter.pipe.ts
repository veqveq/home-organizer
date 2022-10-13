import { Pipe, PipeTransform } from '@angular/core';
import {Dictionary} from "../models/dictionary";

@Pipe({
  name: 'dictionaryFilter'
})
export class DictionaryFilterPipe implements PipeTransform {

  transform(dictionaries: Dictionary[], search: string): Dictionary[] {
    return dictionaries.filter(d => d.name.toLowerCase().includes(search.toLowerCase()));
  }

}
