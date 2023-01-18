import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html'
})
export class LoaderComponent implements OnInit {

  @Input() visible: boolean
  @Input() text: string
  @Input() darkBg: boolean = false
  @Input()size:any = 6

  constructor() {
  }

  ngOnInit(): void {
  }

}
