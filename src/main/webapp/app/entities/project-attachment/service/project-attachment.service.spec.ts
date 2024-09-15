import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IProjectAttachment } from '../project-attachment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../project-attachment.test-samples';

import { ProjectAttachmentService } from './project-attachment.service';

const requireRestSample: IProjectAttachment = {
  ...sampleWithRequiredData,
};

describe('ProjectAttachment Service', () => {
  let service: ProjectAttachmentService;
  let httpMock: HttpTestingController;
  let expectedResult: IProjectAttachment | IProjectAttachment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProjectAttachmentService);
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

    it('should create a ProjectAttachment', () => {
      const projectAttachment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(projectAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProjectAttachment', () => {
      const projectAttachment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(projectAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProjectAttachment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProjectAttachment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProjectAttachment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProjectAttachmentToCollectionIfMissing', () => {
      it('should add a ProjectAttachment to an empty array', () => {
        const projectAttachment: IProjectAttachment = sampleWithRequiredData;
        expectedResult = service.addProjectAttachmentToCollectionIfMissing([], projectAttachment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectAttachment);
      });

      it('should not add a ProjectAttachment to an array that contains it', () => {
        const projectAttachment: IProjectAttachment = sampleWithRequiredData;
        const projectAttachmentCollection: IProjectAttachment[] = [
          {
            ...projectAttachment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProjectAttachmentToCollectionIfMissing(projectAttachmentCollection, projectAttachment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProjectAttachment to an array that doesn't contain it", () => {
        const projectAttachment: IProjectAttachment = sampleWithRequiredData;
        const projectAttachmentCollection: IProjectAttachment[] = [sampleWithPartialData];
        expectedResult = service.addProjectAttachmentToCollectionIfMissing(projectAttachmentCollection, projectAttachment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectAttachment);
      });

      it('should add only unique ProjectAttachment to an array', () => {
        const projectAttachmentArray: IProjectAttachment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const projectAttachmentCollection: IProjectAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addProjectAttachmentToCollectionIfMissing(projectAttachmentCollection, ...projectAttachmentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const projectAttachment: IProjectAttachment = sampleWithRequiredData;
        const projectAttachment2: IProjectAttachment = sampleWithPartialData;
        expectedResult = service.addProjectAttachmentToCollectionIfMissing([], projectAttachment, projectAttachment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectAttachment);
        expect(expectedResult).toContain(projectAttachment2);
      });

      it('should accept null and undefined values', () => {
        const projectAttachment: IProjectAttachment = sampleWithRequiredData;
        expectedResult = service.addProjectAttachmentToCollectionIfMissing([], null, projectAttachment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectAttachment);
      });

      it('should return initial array if no ProjectAttachment is added', () => {
        const projectAttachmentCollection: IProjectAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addProjectAttachmentToCollectionIfMissing(projectAttachmentCollection, undefined, null);
        expect(expectedResult).toEqual(projectAttachmentCollection);
      });
    });

    describe('compareProjectAttachment', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProjectAttachment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProjectAttachment(entity1, entity2);
        const compareResult2 = service.compareProjectAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProjectAttachment(entity1, entity2);
        const compareResult2 = service.compareProjectAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProjectAttachment(entity1, entity2);
        const compareResult2 = service.compareProjectAttachment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
