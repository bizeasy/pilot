import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStatusCategory } from '../status-category.model';
import { StatusCategoryService } from '../service/status-category.service';
import { StatusCategoryFormGroup, StatusCategoryFormService } from './status-category-form.service';

@Component({
  standalone: true,
  selector: 'sys-status-category-update',
  templateUrl: './status-category-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StatusCategoryUpdateComponent implements OnInit {
  isSaving = false;
  statusCategory: IStatusCategory | null = null;

  protected statusCategoryService = inject(StatusCategoryService);
  protected statusCategoryFormService = inject(StatusCategoryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StatusCategoryFormGroup = this.statusCategoryFormService.createStatusCategoryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ statusCategory }) => {
      this.statusCategory = statusCategory;
      if (statusCategory) {
        this.updateForm(statusCategory);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const statusCategory = this.statusCategoryFormService.getStatusCategory(this.editForm);
    if (statusCategory.id !== null) {
      this.subscribeToSaveResponse(this.statusCategoryService.update(statusCategory));
    } else {
      this.subscribeToSaveResponse(this.statusCategoryService.create(statusCategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStatusCategory>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(statusCategory: IStatusCategory): void {
    this.statusCategory = statusCategory;
    this.statusCategoryFormService.resetForm(this.editForm, statusCategory);
  }
}
