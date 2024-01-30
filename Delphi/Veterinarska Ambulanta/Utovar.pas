unit Utovar;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Controls.Presentation, FMX.ScrollBox, FMX.Grid, FMX.Edit,
  FMX.ListBox, FMX.StdCtrls, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Objects;

type
  Tfrmutovar = class(TForm)
    Proizvodi: TComboBox;
    Kolicina: TEdit;
    Proizvod_Label: TLabel;
    Kolicina_Label: TLabel;
    Zavrsi: TButton;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure FormCreate(Sender: TObject);
    procedure ZavrsiClick(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmutovar: Tfrmutovar;
    Row: integer = 0;

implementation

{$R *.fmx}
uses main, inventar;

procedure Tfrmutovar.FormCreate(Sender: TObject);
var
MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Inventar WHERE Tip = ''LEKOVI''';
    MyQuery.Open;

    Proizvodi.Clear;

    while not MyQuery.Eof do
    begin
      Proizvodi.Items.Add(MyQuery.FieldByName('Ime').AsString);
      MyQuery.Next;
    end;
  finally
      MyQuery.Free;
       MyQuery.Close;
  end;
end;

procedure Tfrmutovar.NazadClick(Sender: TObject);
begin
frminventar.show;
self.hide;
end;

procedure TfrmUtovar.ZavrsiClick(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'UPDATE Inventar SET Kolicina = Kolicina + :kolicina WHERE Ime = :ime';

      begin
      MyQuery.ParamByName('ime').AsString := proizvodi.Selected.text;
      MyQuery.ParamByName('kolicina').AsInteger := strtoint(kolicina.Text);
      MyQuery.ExecSQL;
      end;
  finally
    MyQuery.Free;
    ShowMessage('Kolicina promenjena');
    proizvodi.ItemIndex:= -1;
    kolicina.Text := '';
  end;
end;
end.
