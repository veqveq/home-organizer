import {Component, Input, ViewChild} from "@angular/core";
import {Dictionary} from "../../models/dictionary";
import {DictionaryService} from "../../services/dictionary.service";
import {RefDirective} from "../../directives/ref.directive";

@Component({
  selector: 'app-dictionary',
  templateUrl: 'dictionary.component.html'
})
export class DictionaryComponent {
  @Input() dictionary: Dictionary
  @ViewChild(RefDirective) refDir: RefDirective

  constructor(public service: DictionaryService) {
  }
}
