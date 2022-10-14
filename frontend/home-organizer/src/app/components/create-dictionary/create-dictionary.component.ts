import {Component, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormControl, Validators} from "@angular/forms";
import {DictionaryService} from "../../services/dictionary.service";
import {Dictionary} from "../../models/dictionary";
import {ModalService} from "../../services/modal.service";
import {CreateDictionaryFieldComponent} from "../create-dictionary-field/create-dictionary-field.component";
import {RefDirective} from "../../directives/ref.directive";
import {concatMap} from "rxjs";

@Component({
  selector: 'app-create-dictionary',
  templateUrl: './create-dictionary.component.html'
})
export class CreateDictionaryComponent {
  @ViewChild(RefDirective) refDir: RefDirective

  form = this.builder.group({
    name: ['', {
      validators: [
        Validators.required,
        Validators.minLength(3)
      ]
    }],
    description: [''],
    fields: this.builder.array([])
  })

  get name() {
    return this.form.controls.name as FormControl
  }

  get fields() {
    return this.form.controls.fields as FormArray
  }

  constructor(
    private dictionaryService: DictionaryService,
    private modalService: ModalService,
    private builder: FormBuilder
  ) {
  }

  create() {
    this.dictionaryService.create(<Dictionary>this.form.value)
      .pipe(concatMap(() => this.dictionaryService.getAll()))
      .subscribe(() => {
        this.modalService.close()
      })
  }

  addField() {
    const field = this.refDir.containerRef.createComponent(CreateDictionaryFieldComponent)
    field.instance.component = this
    field.instance.index = this.fields.length
    this.fields.push(field.instance.form)
  }

  deleteField(index: number) {
    this.refDir.containerRef.remove(index)
    this.fields.removeAt(index)
  }
}
