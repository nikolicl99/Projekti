unit Pitanje2Podzanr;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Memo.Types,
  FMX.StdCtrls, FMX.Controls.Presentation, FMX.ScrollBox, FMX.Memo, System.Rtti,
  FMX.Grid.Style, FMX.Edit, FMX.Grid, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmPitanje2Podzanr = class(TForm)
    Memo: TMemo;
    Dalje: TButton;
    Nazad: TButton;
    Podzanr_Edit: TEdit;
    Podzanr: TStringGrid;
    Podzanr_col: TStringColumn;
    StyleBook: TStyleBook;
    Forma: TPanel;
    StyleBook1: TStyleBook;
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure FormActivate(Sender: TObject);
    procedure Podzanr_EditChangeTracking(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure DaljeClick(Sender: TObject);
    procedure PodzanrCellClick(const Column: TColumn; const Row: Integer);
    function GetIDPodzanra(const imePodzanra: string): Integer;
    function ProveraNizaPodzanr(const idPodzanra: Integer; const OriginalniNiz: TArray<Integer>): TArray<Integer>;
  private
    { Private declarations }
    SelectedPodzanr: Integer;
  public
    { Public declarations }
    NizPitanje2Podzanr: TArray<Integer>;
  end;

var
  frmPitanje2Podzanr: TfrmPitanje2Podzanr;

implementation

{$R *.fmx}
uses Main, Pitanje2Zanr, Pitanje2Tema, Pitanje1Autor, Pitanje2;

procedure TfrmPitanje2Podzanr.DaljeClick(Sender: TObject);
var
  Poruka: String;
  i: Integer;
begin
  if SelectedPodzanr <> 0 then
  begin
    NizPitanje2Podzanr := ProveraNizaPodzanr(SelectedPodzanr, frmMain.NizKnjige);

//    if Length(NizPitanje2Podzanr) > 0 then
//    begin
//      Poruka := '';
//
//      for i := Low(NizPitanje2Podzanr) to High(NizPitanje2Podzanr) do
//      begin
//        Poruka := Poruka + IntToStr(NizPitanje2Podzanr[i]) + ', ';
//      end;
//
//      SetLength(Poruka, Length(Poruka) - 2);
//      ShowMessage('ID-ovi knjiga: ' + Poruka);
//    end;
    frmPitanje2Tema.Show;
    Self.Hide;
  end
  else
    ShowMessage('Odaberite podzanr');
end;

procedure TfrmPitanje2Podzanr.FormActivate(Sender: TObject);
var
  MyQuery: TFDQuery;
  Row: Integer;
begin
SelectedPodzanr:= 0;
Podzanr_Edit.Text := '';
  Left := Round((Screen.Width - Width) / 2);
  Top := Round((Screen.Height - Height) / 2);
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Podzanr WHERE ZanrID = :Zanr ORDER BY NazivPodzanra';
    MyQuery.ParamByName('Zanr').AsInteger := frmPitanje2Zanr.ZanrID;
    MyQuery.Open;

    Podzanr.RowCount := 1;
    Row := 0;
    while not MyQuery.Eof do
    begin
      Podzanr.RowCount := Podzanr.RowCount + 1;
      Podzanr.Cells[0, Row] := MyQuery.FieldByName('NazivPodzanra').AsString;
      Inc(Row);
      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
    Podzanr.RowCount := Podzanr.RowCount - 1;
  end;
end;

procedure TfrmPitanje2Podzanr.FormClose(Sender: TObject; var Action: TCloseAction);
begin
  Application.Terminate;
end;

procedure TfrmPitanje2Podzanr.NazadClick(Sender: TObject);
begin
  frmPitanje2Zanr.Show;
  Self.Hide;
end;

procedure TfrmPitanje2Podzanr.PodzanrCellClick(const Column: TColumn; const Row: Integer);
begin
  SelectedPodzanr := GetIDPodzanra(Podzanr.Cells[0, Row]);
  Podzanr_Edit.Text := Podzanr.Cells[0, Row];
end;

procedure TfrmPitanje2Podzanr.Podzanr_EditChangeTracking(Sender: TObject);
var
  MyQuery: TFDQuery;
  Row: Integer;
begin
  Left := Round((Screen.Width - Width) / 2);
  Top := Round((Screen.Height - Height) / 2);
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Podzanr WHERE ZanrID = :Zanr AND NazivPodzanra LIKE :Podzanr ORDER BY NazivPodzanra';
    MyQuery.ParamByName('Zanr').AsInteger := frmPitanje2Zanr.ZanrID;
    MyQuery.ParamByName('Podzanr').AsWideString := Podzanr_Edit.Text + '%';
    MyQuery.Open;

    Podzanr.RowCount := 1;
    Row := 0;
    while not MyQuery.Eof do
    begin
      Podzanr.RowCount := Podzanr.RowCount + 1;
      Podzanr.Cells[0, Row] := MyQuery.FieldByName('NazivPodzanra').AsString;
      Inc(Row);
      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

function TfrmPitanje2Podzanr.GetIDPodzanra(const imePodzanra: string): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT IDPodzanra FROM Podzanr WHERE NazivPodzanra = :Podzanr';
    MyQuery.ParamByName('Podzanr').AsWideString := imePodzanra;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('IDPodzanra').AsInteger;
  finally
    MyQuery.Free;
  end

end;


function TfrmPitanje2Podzanr.ProveraNizaPodzanr(const idPodzanra: Integer; const OriginalniNiz: TArray<Integer>): TArray<Integer>;
var
  MyQuery: TFDQuery;
  ResultArray: TArray<Integer>;
  i: Integer;
  Found: Boolean;
  KnjigaID: Integer;
begin
  SetLength(ResultArray, 0);

  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT IDKnjige FROM Knjige WHERE Podzanr = :Podzanr';
    MyQuery.ParamByName('Podzanr').AsInteger := idPodzanra;
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      KnjigaID := MyQuery.FieldByName('IDKnjige').AsInteger;

      Found := False;
      for i := Low(OriginalniNiz) to High(OriginalniNiz) do
      begin
        if KnjigaID = OriginalniNiz[i] then
        begin
          Found := True;
          Break;
        end;
      end;

      if Found then
      begin
        SetLength(ResultArray, Length(ResultArray) + 1);
        ResultArray[Length(ResultArray) - 1] := KnjigaID;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;

  Result := ResultArray;
end;


end.

