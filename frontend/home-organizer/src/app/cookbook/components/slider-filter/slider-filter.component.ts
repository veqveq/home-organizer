import {
  Component,
  DoCheck,
  EventEmitter,
  Input,
  KeyValueDiffer,
  KeyValueDiffers,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {Options} from "ng5-slider";
import {RecipeService} from "../../services/recipe.service";
import {SliderComponent} from "ng5-slider/slider.component";

@Component({
  selector: 'app-slider-filter',
  templateUrl: './slider-filter.component.html'
})
export class SliderFilterComponent implements OnInit, DoCheck {
  @ViewChild('slider') slider: SliderComponent
  @Input() title: string
  @Input() from: number
  @Input() to: number
  @Input() endpoint: string
  @Output() filterFrom = new EventEmitter();
  @Output() filterTo = new EventEmitter();
  @Output() isChanged = new EventEmitter();

  private differ: KeyValueDiffer<string, any>

  hasChanges: boolean = false
  options: Options


  constructor(
    private differs: KeyValueDiffers,
    public service: RecipeService
  ) {
    this.differ = differs.find({}).create()
  }

  ngOnInit(): void {
    this.options = {
      floor: this.from,
      ceil: this.to ? this.to : 0,
      noSwitching: true,
      hideLimitLabels: true,
      hidePointerLabels: true
    };
    if (this.endpoint) {
      this.service.getLimit(this.endpoint).subscribe(value => {
        this.setNewCeil(value)
      })
    }
  }

  ngDoCheck(): void {
    const change = this.differ.diff(this)
    if (change) {
      change.forEachChangedItem(item => {
        if (item.key == 'from') {
          this.changeFrom()
        } else if (item.key == 'to') {
          this.changeTo()
        } else if (item.key == 'hasChanges') {
          this.changed()
        }
      });
    }
  }

  private changeFrom() {
    this.hasChanges = this.slider.value != this.slider.options.floor || this.slider.highValue != this.slider.options.ceil;
    this.filterFrom.emit(this.from)
  }

  private changeTo() {
    this.hasChanges = this.slider.value != this.slider.options.floor || this.slider.highValue != this.slider.options.ceil;
    this.filterTo.emit(this.to)
  }

  private changed() {
    this.isChanged.emit(this.hasChanges)
  }

  setNewCeil(newCeil: number): void {
    const newOptions: Options = Object.assign({}, this.options);
    this.to = newCeil
    newOptions.ceil = newCeil;
    this.options = newOptions;
  }

  reset() {
    this.hasChanges = false
    this.slider.value = this.slider.options.floor
    this.slider.highValue = this.slider.options.ceil
    this.from = this.slider.options.floor
    this.to = this.slider.options.ceil
  }
}
