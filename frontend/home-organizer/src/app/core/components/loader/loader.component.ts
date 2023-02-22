import {Component, Input, OnInit} from '@angular/core';
import {LoaderPosition} from "./loader-position-enum";

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html'
})
export class LoaderComponent implements OnInit {

  @Input() visible: boolean
  @Input() text: string
  @Input() darkBg: boolean = false
  @Input()size:number = 6
  @Input() loaderPosition: LoaderPosition = LoaderPosition.RIGHT

  public LoaderPositionEnum = LoaderPosition


  constructor() {
  }

  ngOnInit(): void {
  }

}
