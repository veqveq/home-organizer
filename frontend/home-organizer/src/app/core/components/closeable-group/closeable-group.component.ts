import {Component, EventEmitter, Input, Output} from '@angular/core';
import {OnNamed} from "../../models/onNamed";

@Component({
  selector: 'app-closeable-group',
  templateUrl: './closeable-group.component.html'
})
export class CloseableGroupComponent<G extends OnNamed, T extends OnNamed> {
  @Input() group: G
  @Input() elements: T[]
  @Output() elementClosed: EventEmitter<T> = new EventEmitter<T>()
  @Output() groupClosed: EventEmitter<G> = new EventEmitter<G>()
  open: boolean

  nextView(){
    this.open = !this.open
  }


}
