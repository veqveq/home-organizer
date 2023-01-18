import {Component, EventEmitter, Input, Output, ViewChild} from "@angular/core";
import {Dictionary} from "../../models/dictionary";
import {DictionaryService} from "../../services/dictionary.service";
import {RefDirective} from "../../../core/directives/ref.directive";
import {tap} from "rxjs";

@Component({
  selector: 'app-dictionary',
  templateUrl: 'dictionary.component.html'
})
export class DictionaryComponent {
  @Input() dictionary: Dictionary
  @Output() needUpdate = new EventEmitter()
  @ViewChild(RefDirective) refDir: RefDirective

  constructor(public service: DictionaryService) {
  }

  delete() {
    this.service.delete(this.dictionary)
      .pipe(tap(() => {
        this.needUpdate.emit()
      }))
      .subscribe()
  }
}
