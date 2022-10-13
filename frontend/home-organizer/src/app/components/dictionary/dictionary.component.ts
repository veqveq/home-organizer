import {Component, Input} from "@angular/core";
import {Dictionary} from "../../models/dictionary";

@Component({
  selector: 'app-dictionary',
  templateUrl: 'product.component.html'
})
export class DictionaryComponent{
  @Input() dictionary: Dictionary
}
