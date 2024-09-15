import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISprintTask } from '../sprint-task.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sprint-task.test-samples';

import { RestSprintTask, SprintTaskService } from './sprint-task.service';

const requireRestSample: RestSprintTask = {
  ...sampleWithRequiredData,
  fromTime: sampleWithRequiredData.fromTime?.toJSON(),
  thruTime: sampleWithRequiredData.thruTime?.toJSON(),
  assignedTime: sampleWithRequiredData.assignedTime?.toJSON(),
};

describe('SprintTask Service', () => {
  let service: SprintTaskService;
  let httpMock: HttpTestingController;
  let expectedResult: ISprintTask | ISprintTask[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SprintTaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SprintTask', () => {
      const sprintTask = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sprintTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SprintTask', () => {
      const sprintTask = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sprintTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SprintTask', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SprintTask', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SprintTask', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSprintTaskToCollectionIfMissing', () => {
      it('should add a SprintTask to an empty array', () => {
        const sprintTask: ISprintTask = sampleWithRequiredData;
        expectedResult = service.addSprintTaskToCollectionIfMissing([], sprintTask);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprintTask);
      });

      it('should not add a SprintTask to an array that contains it', () => {
        const sprintTask: ISprintTask = sampleWithRequiredData;
        const sprintTaskCollection: ISprintTask[] = [
          {
            ...sprintTask,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSprintTaskToCollectionIfMissing(sprintTaskCollection, sprintTask);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SprintTask to an array that doesn't contain it", () => {
        const sprintTask: ISprintTask = sampleWithRequiredData;
        const sprintTaskCollection: ISprintTask[] = [sampleWithPartialData];
        expectedResult = service.addSprintTaskToCollectionIfMissing(sprintTaskCollection, sprintTask);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprintTask);
      });

      it('should add only unique SprintTask to an array', () => {
        const sprintTaskArray: ISprintTask[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sprintTaskCollection: ISprintTask[] = [sampleWithRequiredData];
        expectedResult = service.addSprintTaskToCollectionIfMissing(sprintTaskCollection, ...sprintTaskArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sprintTask: ISprintTask = sampleWithRequiredData;
        const sprintTask2: ISprintTask = sampleWithPartialData;
        expectedResult = service.addSprintTaskToCollectionIfMissing([], sprintTask, sprintTask2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprintTask);
        expect(expectedResult).toContain(sprintTask2);
      });

      it('should accept null and undefined values', () => {
        const sprintTask: ISprintTask = sampleWithRequiredData;
        expectedResult = service.addSprintTaskToCollectionIfMissing([], null, sprintTask, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprintTask);
      });

      it('should return initial array if no SprintTask is added', () => {
        const sprintTaskCollection: ISprintTask[] = [sampleWithRequiredData];
        expectedResult = service.addSprintTaskToCollectionIfMissing(sprintTaskCollection, undefined, null);
        expect(expectedResult).toEqual(sprintTaskCollection);
      });
    });

    describe('compareSprintTask', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSprintTask(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSprintTask(entity1, entity2);
        const compareResult2 = service.compareSprintTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSprintTask(entity1, entity2);
        const compareResult2 = service.compareSprintTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSprintTask(entity1, entity2);
        const compareResult2 = service.compareSprintTask(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
