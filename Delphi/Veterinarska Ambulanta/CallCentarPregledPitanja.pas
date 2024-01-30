unit CallCentarPregledPitanja;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Error, FireDAC.UI.Intf, FireDAC.Phys.Intf, FireDAC.Stan.Def,
  FireDAC.Stan.Pool, FireDAC.Stan.Async, FireDAC.Phys, FireDAC.Phys.SQLite,
  FireDAC.Phys.SQLiteDef, FireDAC.Stan.ExprFuncs,
  FireDAC.Phys.SQLiteWrapper.Stat, FireDAC.VCLUI.Wait, Data.DB,
  FireDAC.Comp.Client, Vcl.StdCtrls, Vcl.Grids, FireDAC.Stan.Param,
  FireDAC.DatS, FireDAC.DApt.Intf, FireDAC.DApt, FireDAC.Comp.DataSet,
  Vcl.Imaging.pngimage, Vcl.ExtCtrls;

type
  TCCForm4 = class(TForm)
    Label1: TLabel;
    Label2: TLabel;
    StringGrid1: TStringGrid;
    Button2: TButton;
    Image2: TImage;
    procedure Button2Click(Sender: TObject);
    procedure FormCreate(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  CCForm4: TCCForm4;

implementation

{$R *.dfm}

uses CallCentarOdgovorVeterinara, main;


procedure TCCForm4.Button2Click(Sender: TObject);
begin
CCForm3.show;
self.hide;
end;

procedure TCCForm4.FormCreate(Sender: TObject);
var
  RowIndex: Integer;
  MyQuery: TFDQuery;
begin
  // Clear existing data in the string grid
  StringGrid1.RowCount := 1;

  MyQuery := TFDQuery.Create(nil);
  MyQuery.Connection := GlobalConnection;
  // Set the column count and headers in the string grid
  StringGrid1.ColCount := 5;
  StringGrid1.Cells[0, 0] := 'IDPitanja';
  StringGrid1.Cells[1, 0] := 'IDKlijent';
  StringGrid1.Cells[2, 0] := 'Datum_Prijema';
  StringGrid1.Cells[3, 0] := 'Vreme_Prijema';
  StringGrid1.Cells[4, 0] := 'Pitanje';

  MyQuery.SQL.Text :='SELECT * FROM Call_Centar';
  MyQuery.Open;

  // Loop through the query result and populate the string grid
  RowIndex := 1;
  while not MyQuery.Eof do
  begin
    StringGrid1.RowCount := RowIndex + 1;
    StringGrid1.Cells[0, RowIndex] := MyQuery.FieldByName('IDPitanja').AsString;
    StringGrid1.Cells[1, RowIndex] := MyQuery.FieldByName('IDKlijent').AsString;
    StringGrid1.Cells[2, RowIndex] := MyQuery.FieldByName('Datum_Prijema').AsString;
    StringGrid1.Cells[3, RowIndex] := MyQuery.FieldByName('Vreme_Prijema').AsString;
    StringGrid1.Cells[4, RowIndex] := MyQuery.FieldByName('Pitanje').AsString;

    MyQuery.Next;
    Inc(RowIndex);
  end;

  // Close the query
  MyQuery.Close;
end;

end.

