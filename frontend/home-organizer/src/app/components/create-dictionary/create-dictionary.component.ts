import {Component, ComponentFactoryResolver, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {DictionaryService} from "../../services/dictionary.service";
import {DictionaryComponent} from "../dictionary/dictionary.component";
import {Dictionary} from "../../models/dictionary";
import {catchError, throwError} from "rxjs";
import {ModalService} from "../../services/modal.service";
import {ErrorService} from "../../services/error.service";
import {HttpErrorResponse} from "@angular/common/http";
import {DictionaryField} from "../../models/dictionary-field";
import {CreateDictionaryFieldComponent} from "../create-dictionary-field/create-dictionary-field.component";
import {RefDirective} from "../../directives/ref.directive";

@Component({
  selector: 'app-create-dictionary',
  templateUrl: './create-dictionary.component.html'
})
export class CreateDictionaryComponent {
  @ViewChild(RefDirective) refDir: RefDirective

  form = new FormGroup({
    name: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    fields: new FormControl<DictionaryField[]>([])
  })

  get name() {
    return this.form.controls.name as FormControl
  }

  constructor(
    private dictionaryService: DictionaryService,
    private modalService: ModalService
  ) {
  }

  send() {
    console.log(this.form.value)
    // this.dictionaryService.create(<Dictionary>this.form.value)
    //   .subscribe(() => {
    //     this.modalService.close()
    //     this.dictionaryService.getAll()
    //   })
  }

  addField() {
    const field = this.refDir.containerRef.createComponent(CreateDictionaryFieldComponent)
    this.form.controls.fields.value?.push(field.instance.get())
  }
}
