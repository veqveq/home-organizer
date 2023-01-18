import {Component, Input, OnInit, Output} from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
})
export class ModalComponent {
  @Input() title: string

  public visible$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false)

  constructor() {
  }

  public open() {
    this.visible$.next(true)
  }

  public close() {
    this.visible$.next(false)
  }
}
