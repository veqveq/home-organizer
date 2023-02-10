import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';

@Component({
  selector: 'app-dropdown-tab',
  templateUrl: './dropdown-tab.component.html'
})
export class DropdownTabComponent implements OnInit {
  @ViewChild('content') content: ElementRef
  @Output() reset = new EventEmitter()
  @Input() title: string
  @Input() enabled: Boolean = true
  @Input() openOnlyButton: Boolean = false

  public dropdown: boolean = false
  changeCounter: number = 0

  constructor() {
  }

  ngOnInit(): void {
  }

  isOpen() {
    return this.dropdown
  }

  hasChanges(res: boolean) {
    if (res) {
      this.changeCounter++
    } else {
      this.changeCounter--
    }
  }

  doReset() {
    this.closeList()
    this.reset.emit()
  }

  nextDropdown(){
    if (!this.content.nativeElement.classList.contains('show')) {
      this.openList()
    } else {
      this.closeList()
    }
  }

  private openList(){
    this.dropdown = true
    let dropdownContent = this.content.nativeElement
    dropdownContent.classList.add('show');
    dropdownContent.style.height = 'auto';
    var height = dropdownContent.clientHeight + 'px';
    dropdownContent.style.height = '0px';
    setTimeout(function () {
      dropdownContent.style.height = height;
    }, 0);
  }

  private closeList(){
    this.dropdown = false
    let dropdownContent = this.content.nativeElement
    dropdownContent.style.height = '0px';
    dropdownContent.addEventListener('transitionend',
      function () {
        dropdownContent.classList.remove('show');
      }, {
        once: true
      });
  }
}
